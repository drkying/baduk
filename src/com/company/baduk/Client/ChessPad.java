package com.company.baduk.Client;

import com.company.baduk.DataStruct.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChessPad extends JPanel implements MouseListener, ActionListener {
    private Player nowPlayer;
    private PlayerSocket playerSocket;
    private boolean isYourTurn = false;
    private Point[][] now = new Point[21][21];
    private List<Point[][]> chessHistory = new ArrayList<>();
    private List<String> chessHash = new ArrayList<>();
    int dim = 19;   //19X19的棋盘
    int[] block;
    int blockLength = 0, score_B = 0, score_W = 0;

    public ChessPad(Player nowPlayer, PlayerSocket playerSocket) {
        initBorder();
        this.nowPlayer = nowPlayer;
        this.playerSocket = playerSocket;

        if (nowPlayer == Player.BLACK) {
            isYourTurn = true;
            Begin.label.setText("现在是你下棋");
        } else {
            isYourTurn = false;
            Begin.label.setText("等待对方下棋。。。");
        }


        this.setBackground(Color.ORANGE);
        setSize(440, 440);
        addMouseListener(this);
        setLayout(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                readMessage();
            }
        }).start();
        backupBorder();
    }

    public void backupBorder() {
        chessHistory.add(now);
        chessHash.add(Tool.sumHashCode(now));
    }

    public void restoreBorder() {
        Point[][] lastChessPad = chessHistory.get(chessHistory.size() - 1);
        chessHistory.remove(chessHistory.size() - 1);
        this.removeAll();
        for (int i = 2; i <= 20; i++)
            for (int j = 2; j <= 20; j++) {
                if (lastChessPad[i][j].getPlayer() == Player.NONE) continue;
                this.add(lastChessPad[i][j]);
                lastChessPad[i][j].setBounds(lastChessPad[i][j].getX() * 20 - 10, lastChessPad[i][j].getY() * 20 - 10, 20, 20);
            }
        chessHash.remove(chessHash.size() - 1);
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }

    //网络请求数据
    public void readMessage() {
        try {
            Object temp;
            Point point;
            while ((temp = playerSocket.read()) != null) {
                UpdateMessages msg = (UpdateMessages) temp;
                switch (msg) {
                    case ADD_POINT:
                        temp = playerSocket.read();
                        point = (Point) temp;
                        addPoint(point);
                        isYourTurn = true;
                        Begin.label.setText("现在到你下棋了！");
                        break;
                    case CLIENT_GIVE_UP:
                        JOptionPane.showMessageDialog(null, "对方认输，你赢了！");
                        JOptionPane.showMessageDialog(null, "游戏结束！");
                        System.exit(0);
                        break;
                    case CLIENT_PASS:
                        JOptionPane.showMessageDialog(null, "对方跳过下棋，现在到你了！");
                        isYourTurn = true;
                        Begin.label.setText("现在到你下棋了！");
                        break;
                    case RECVD_MOVE:
                        int n = JOptionPane.showConfirmDialog(null, "请确认:\n对方请求悔棋，是否同意?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                        if (n == 0) {
                            restoreBorder();
                            isYourTurn = false;
                            Begin.label.setText("等待对方下棋。。。");
                            System.out.println("我同意对方悔棋");
                            playerSocket.write(UpdateMessages.AGREE_RECVD);
                            System.out.println("发送我同意对方悔棋");
                        } else System.out.println("我不同意对方悔棋");
                        break;
                    case AGREE_RECVD:
                        JOptionPane.showMessageDialog(null, "对方同意悔棋，现在到你了！");
                        restoreBorder();
                        isYourTurn = true;
                        Begin.label.setText("现在到你下棋了！");
                        break;
                    case GAME_FINISH:
                        JOptionPane.showMessageDialog(null, "双方都跳过下棋，比赛结束！\n最终成绩：\n黑：0  白：0\n黑棋赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                        break;
                }
                updateScore();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void removePoint(Point point) {
        remove(now[point.getX()][point.getY()]);
        now[point.getX()][point.getY()].setPlayer(Player.NONE);
    }

    public void addPoint(Point point) {
        if (check(point.getX(), point.getY())) {
            backupBorder();
            this.add(point);
            now[point.getX()][point.getY()] = point;
            checkDelete(point.getX(), point.getY());
        }
    }

    public void addPoint(int x, int y) {
        int a = (x + 10) / 20, b = (y + 10) / 20;
        if (isYourTurn && check(a, b)) {
            backupBorder();
            if (nowPlayer == Player.BLACK) {
                now[a][b].setPlayer(Player.BLACK);
            } else if (nowPlayer == Player.WHITE) {
                now[a][b].setPlayer(Player.WHITE);
            } else {
                return;
            }
            if (x / 20 < 2 || y / 20 < 2 || x / 20 > 19 || y / 20 > 19) {
            } else {
                this.add(now[a][b]);
                now[a][b].setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
                chessHistory.add(now);
                playerSocket.write(UpdateMessages.ADD_POINT);
                playerSocket.write(now[a][b]);
                isYourTurn = false;
                Begin.label.setText("等待对方下棋。。。");
                checkDelete(now[a][b].getX(), now[a][b].getY());
            }
        }

    }

    public void checkDelete(int x, int y) {
        for (int i = 2; i < 21; i++) {
            for (int j = 2; j < 21; j++) {
                if (now[i][j].getPlayer() == Player.NONE)
                    continue;
                else {
                    block = new int[361];
                    blockLength = 1;
                    block[0] = i * 100 + j;

                    recursion(i, j);

                    if (hasQi())
                        continue;
                    else {
                        for (int t = 0; t < blockLength; t++)
                            removePoint(now[block[t] / 100][block[t] % 100]);
                    }
                }
            }
        }
        updateScore();
    }

    public void recursion(int i, int j) {
        //Left
        if (i - 1 >= 2 && now[i - 1][j].getPlayer() == now[i][j].getPlayer() && isInBlock((i - 1) * 100 + j)) {
            block[blockLength] = (i - 1) * 100 + j;
            blockLength++;
            recursion(i - 1, j);
        }
        //Up
        if (j - 1 >= 2 && now[i][j - 1].getPlayer() == now[i][j].getPlayer() && isInBlock(i * 100 + j - 1)) {
            block[blockLength] = i * 100 + j - 1;
            blockLength++;
            recursion(i, j - 1);
        }
        //Right
        if (i + 1 < 21 && now[i + 1][j].getPlayer() == now[i][j].getPlayer() && isInBlock((i + 1) * 100 + j)) {
            block[blockLength] = (i + 1) * 100 + j;
            blockLength++;
            recursion(i + 1, j);
        }
        //Down
        if (j + 1 < 21 && now[i][j + 1].getPlayer() == now[i][j].getPlayer() && isInBlock(i * 100 + j + 1)) {
            block[blockLength] = i * 100 + j + 1;
            blockLength++;
            recursion(i, j + 1);
        }
    }

    public boolean hasQi() {
        int i, j;
        for (int t = 0; t < blockLength; t++) {
            i = block[t] / 100;
            j = block[t] % 100;
            if (i - 1 >= 2 && now[i - 1][j].getPlayer() == Player.NONE) return true;
            if (i + 1 < 21 && now[i + 1][j].getPlayer() == Player.NONE) return true;
            if (j - 1 >= 2 && now[i][j - 1].getPlayer() == Player.NONE) return true;
            if (j + 1 < 21 && now[i][j + 1].getPlayer() == Player.NONE) return true;
        }
        return false;
    }

    public boolean isInBlock(int neighbor) {
        for (int i = 0; i < blockLength; i++) {
            if (block[i] == neighbor) return false;
        }
        return true;
    }

    public void updateScore() {
        Begin.score.setText("计分板：" + "黑：" + score_B + " " + "白： " + score_W);
        for (int i = 2; i <= 20; i++) {
            for (int j = 2; j <= 20; j++) {
                if (now[i][j].getPlayer() == Player.BLACK)
                    System.out.print("[" + "●" + "]");
                else if (now[i][j].getPlayer() == Player.WHITE)
                    System.out.print("[" + "○" + "]");
                else if (now[i][j].getPlayer() == Player.NONE)
                    System.out.print("[" + " " + "]");
            }
            System.out.println("");
        }
    }

    public boolean check(int x, int y) {
        now[x][y].setPlayer(nowPlayer);
        String temp = Tool.sumHashCode(now);
        for (int i = 0; i < chessHash.size(); i++) {
            if (chessHash.get(i).equals(temp)) {
                che:
                for (int j = 2; j <= 20; j++) {
                    for (int k = 2; k <= 20; k++) {
                        if (chessHistory.get(i)[j][k] != now[j][k])
                            break che;
                        if (j == 20 && k == 20) {
                            JOptionPane.showMessageDialog(null, "当前下棋位置不合法，请重新下棋");
                            now[x][y].setPlayer(Player.NONE);
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

    public void cpyBorder(Point[][] src, Point[][] dst) {
        for (int i = 2; i <= dim + 1; i++)
            for (int j = 2; j <= dim + 1; j++) {
                dst[i][j] = src[i][j];
            }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK) {
            int x = e.getX();
            int y = e.getY();
            addPoint(x, y);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 40; i <= 400; i += 20) {
            g.drawLine(40, i, 400, i);
        }
        for (int j = 40; j <= 400; j += 20) {
            g.drawLine(j, 40, j, 400);
        }
        g.fillOval(97, 97, 6, 6);
        g.fillOval(97, 337, 6, 6);
        g.fillOval(337, 97, 6, 6);
        g.fillOval(337, 337, 6, 6);
        g.fillOval(217, 217, 6, 6);
    }

    public void initBorder() {
        for (int i = 2; i <= dim + 1; i++)
            for (int j = 2; j <= dim + 1; j++) {
                now[i][j] = new Point(Player.NONE, i, j);
            }
    }
}

