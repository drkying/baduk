package com.company.baduk.Client;

import com.company.baduk.DataStruct.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChessPad extends JPanel implements MouseListener, ActionListener {
    public Player nowPlayer;
    public boolean blackGroup;
    public boolean whiteGroup;
    public int currentGroup = 0;
    public PlayerSocket playerSocket;
    public boolean isYourTurn;
    public Point[][] now = new Point[21][21];
    public Point[][] previous = new Point[21][21];
    public int dim = 19;   //19X19的棋盘
    public int score_B = 0, score_W = 0;
    public static int W_eat_B = 0, B_eat_W = 0;
    public GoRules goRules;
    public myHashTable chessPadHistory;

    //初始化棋盘
    public ChessPad(Player nowPlayer, PlayerSocket playerSocket) {
        initBorder();
        this.nowPlayer = nowPlayer;
        this.playerSocket = playerSocket;
        this.goRules = new GoRules(this);
        this.chessPadHistory = new myHashTable();

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

    // 对每一步的棋局进行备份
    public void backupBorder() {
        previous = new Point[21][21];
        previous = Tool.clone(now);
        chessPadHistory.add(previous);
        updateScore();
    }

    //恢复棋局，用于悔棋操作
    public void restoreBorder() {
        Point[][] lastChessPad = chessPadHistory.pop();

        this.removeAll();
        for (int i = 2; i <= 20; i++)
            for (int j = 2; j <= 20; j++) {
                if (lastChessPad[i][j].getPlayer() == Player.NONE) continue;
                this.add(lastChessPad[i][j]);
                lastChessPad[i][j].setBounds(lastChessPad[i][j].getX() * 20 - 10, lastChessPad[i][j].getY() * 20 - 10, 20, 20);
            }
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }


    //删除棋子
    public void removePoint(Point point, Point[][] temp) {
        int x = point.getX(), y = point.getY();
        this.remove(temp[point.getX()][point.getY()]);
        temp[x][y] = new Point(Player.NONE, x, y);
    }


    //添加棋子
    public void addPoint(Point point) {
        this.add(point);
        now[point.getX()][point.getY()] = point;
        goRules.checkDelete(point.getX(), point.getY(), now);
        backupBorder();
    }

    public void addPoint(int x, int y) {
        if (isYourTurn) {
            int a = (x + 10) / 20, b = (y + 10) / 20;
            if (now[a][b].getPlayer() != Player.NONE) {
                JOptionPane.showMessageDialog(null, "此处已经有棋子了！");
                return;
            }
            if (x / 20 < 2 || y / 20 < 2 || x / 20 >= 21 || y / 20 >= 21) {
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

                Point[][] future = Tool.clone(now);
                goRules.checkDelete(future[a][b].getX(), future[a][b].getY(), future, false);

                if (!check(future)) {
                    JOptionPane.showMessageDialog(null, "当前下棋位置导致重局，请重新下棋");
                    now[a][b].setPlayer(Player.NONE);
                    return;
                }
                this.add(now[a][b]);
                goRules.checkDelete(now[a][b].getX(), now[a][b].getY(), now);
                now[a][b].setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
                backupBorder();
                playerSocket.write(UpdateMessages.ADD_POINT);
                playerSocket.write(now[a][b]);
                isYourTurn = false;
                Begin.label.setText("等待对方下棋。。。");
            }
        }
    }

    //更新计分板
    public void updateScore() {
        calcTerritory();
        int score_B = this.score_B + B_eat_W, score_W = this.score_W + W_eat_B;
        System.out.println("1:" + this.score_B + ";2:" + B_eat_W + ";3:" + this.score_W + ";4:" + W_eat_B);
        Begin.score.setText("计分板：" + "黑：" + score_B + " " + "白： " + score_W);
    }

    //判断是否出现重局的情况
    public boolean check(Point[][] future) {
        String temp = myHashTable.hashFun(future);
        int id;
        Point[][] past;
        if ((id = chessPadHistory.find(temp)) != -1)
            past = chessPadHistory.get(id);
        else return true;
        for (int j = 2; j <= 20; j++) {
            for (int k = 2; k <= 20; k++) {
                if (!past[j][k].equals(future[j][k]))
                    return true;
                if (j == 20 && k == 20) {
                    return false;
                }
            }
        }
        return true;
    }


    //计算双方的得分
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

    //绘制 19 X 19 的棋盘
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
                        if (score_B > score_W)
                            JOptionPane.showMessageDialog(null, "双方都跳过下棋，比赛结束！\n最终成绩：\n黑：" + score_B + "  白：" + score_W + "\n黑棋赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                        else if (score_B < score_W)
                            JOptionPane.showMessageDialog(null, "双方都跳过下棋，比赛结束！\n最终成绩：\n黑：" + score_B + "  白：" + score_W + "\n白棋赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                        else if (score_W == score_B)
                            JOptionPane.showMessageDialog(null, "双方都跳过下棋，比赛结束！\n最终成绩：\n黑：" + score_B + "  白：" + score_W + "\n双方平局！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);


                        System.exit(0);
                        break;
                }
                updateScore();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "与对方的网络连接断开，游戏结束！");
            System.exit(0);
        }
    }
}

