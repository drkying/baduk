package com.company.baduk.Client;

import com.company.baduk.DataStruct.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChessPad extends JPanel implements MouseListener, ActionListener {
    public Player nowPlayer;
    public boolean blackGroup;
    public boolean whiteGroup;
    public int currentGroup = 0;
    public PlayerSocket playerSocket;
    public boolean isYourTurn = false;
    public Point[][] now = new Point[21][21];
    public Point[][] previous = new Point[21][21];
    public List<Point[][]> chessHistory = new ArrayList<>();
    public List<String> chessHash = new ArrayList<>();
    public int dim = 19;   //19X19的棋盘
    public int[] block;
    public int blockLength = 0, score_B = 0, score_W = 0;
    public GoRules goRules;

    public ChessPad(Player nowPlayer, PlayerSocket playerSocket) {
        initBorder();
        this.nowPlayer = nowPlayer;
        this.playerSocket = playerSocket;
        this.goRules = new GoRules(this);

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
        previous = new Point[21][21];
        cpyBorder(now, previous);
        chessHistory.add(previous);
        chessHash.add(Tool.sumHashCode(previous));
        updateScore();
    }

    public void restoreBorder() {
        Point[][] lastChessPad = chessHistory.get(chessHistory.size() - 2);
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



    public void removePoint(Point point) {
        int x = point.getX(), y = point.getY();
        remove(now[point.getX()][point.getY()]);
        now[x][y] = new Point(Player.NONE, x, y);
    }

    public void addPoint(Point point) {
        if (check(point.getX(), point.getY())) {
            this.add(point);
            now[point.getX()][point.getY()] = point;
            goRules.checkDelete(point.getX(), point.getY());
            backupBorder();
        }
    }

    public void addPoint(int x, int y) {
        if (isYourTurn) {
            int a = (x + 10) / 20, b = (y + 10) / 20;
            if (now[a][b].getPlayer() != Player.NONE) {
                JOptionPane.showMessageDialog(null, "此处已经有棋子了！");
                return;
            }
            if (!check(a, b)) {
                JOptionPane.showMessageDialog(null, "当前下棋位置导致重局，请重新下棋");
                return;
            }
            if (x / 20 < 2 || y / 20 < 2 || x / 20 > 19 || y / 20 > 19) {
                JOptionPane.showMessageDialog(null, "不能下在此处！");
                return;
            } else {
                if (nowPlayer == Player.BLACK) {
                    now[a][b].setPlayer(Player.BLACK);
                } else if (nowPlayer == Player.WHITE) {
                    now[a][b].setPlayer(Player.WHITE);
                } else {
                    return;
                }
                this.add(now[a][b]);
                now[a][b].setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
                goRules.checkDelete(now[a][b].getX(), now[a][b].getY());

                backupBorder();
                playerSocket.write(UpdateMessages.ADD_POINT);
                playerSocket.write(now[a][b]);
                isYourTurn = false;
                Begin.label.setText("等待对方下棋。。。");
            }

        }

    }

    public void updateScore() {
        calcTerritory();
        Begin.score.setText("计分板：" + "黑：" + score_B + " " + "白： " + score_W);
        System.out.println("now chessPad:");
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
                            now[x][y].setPlayer(Player.NONE);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void calcTerritory() {
        int[][] mark = new int[dim + 2][dim + 2];
        score_B = 0;
        score_W = 0;
        currentGroup = 0;
        for (int y = 2; y < dim + 2; y++) {
            for (int x = 2; x < dim + 2; x++) {
                blackGroup = false;
                whiteGroup = false;
                currentGroup++;
                markGroup(y, x, mark);  //同时使用广度优先遍历和FloodFill算法标记相同颜色的棋子
                int cnt = 0;
                for (int[] ia : mark) {
                    for (int val : ia) {
                        if (val == currentGroup) {
                            cnt++;
                        }
                    }
                }
                if (blackGroup && !whiteGroup) {
                    score_B += cnt;
                } else if (whiteGroup && !blackGroup) {
                    score_W += cnt;
                }
            }
        }
    }

    public void markGroup(int y, int x, int[][] mark) {
        if (y < 2 || y >= dim + 2 || x < 2 || x >= dim + 2
                || mark[y][x] > 0) {
            return;
        } else if (mark[y][x] == -1) {
            blackGroup = true;
        } else if (mark[y][x] == -2) {
            whiteGroup = true;
        } else if (now[y][x].getPlayer() == Player.BLACK) {
            blackGroup = true;
            mark[y][x] = -1;
        } else if (now[y][x].getPlayer() == Player.WHITE) {
            whiteGroup = true;
            mark[y][x] = -2;
        } else {
            mark[y][x] = currentGroup;
            markGroup(y, x - 1, mark);
            markGroup(y - 1, x, mark);
            markGroup(y, x + 1, mark);
            markGroup(y + 1, x, mark);
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

    public void cpyBorder(Point[][] src, Point[][] dst) {
        for (int i = 2; i <= dim + 1; i++)
            for (int j = 2; j <= dim + 1; j++) {
                dst[i][j] = src[i][j];
            }
    }
}

