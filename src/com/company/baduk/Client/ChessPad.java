package com.company.baduk.Client;

import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.UpdateMessages;
import com.company.baduk.DataStruct.myStack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class ChessPad extends JPanel implements MouseListener, ActionListener {
    private Player nowPlayer = Player.BLACK;
    private boolean isYourTurn = false;
    private myStack<Point[][]> pointsHistory;
    private Point[][] now = new Point[21][21];
    private Point[][] previous = new Point[21][21];
    private LinkedList<Point[][]> pointsList = new LinkedList<>();
    private List hash = new List();
    int dim = 19;

    public ChessPad(Player nowPlayer) {
        this.nowPlayer = nowPlayer;
        if (nowPlayer == Player.BLACK) {
            isYourTurn = true;
            Begin.label.setText("现在是你下棋");
        } else {
            isYourTurn = false;
            Begin.label.setText("等待对方下棋。。。");
        }
        pointsHistory = new myStack<>();
        this.setBackground(Color.ORANGE);
        setSize(440, 440);
        addMouseListener(this);
        setLayout(null);
        initBorder();
        new Thread(new Runnable() {
            @Override
            public void run() {
                readMessage();
            }
        }).start();
        backupBorder();
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
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

    public void removePoints() {
        this.removeAll();
        pointsHistory = new myStack<>();
    }


    public void cpyBorder(Point[][] src, Point[][] dst) {
        for (int i = 2; i <= dim + 1; i++)
            for (int j = 2; j <= dim + 1; j++) {
                dst[i][j] = src[i][j];
            }
    }

    public void initBorder() {
        for (int i = 2; i <= dim + 1; i++)
            for (int j = 2; j <= dim + 1; j++) {
                now[i][j] = new Point(Player.NONE, i, j);
            }
    }

    public void backupBorder() {
        cpyBorder(now, previous);
    }

    public void restoreBorder() {
        removePoints();
        cpyBorder(previous, now);
        for (int i = 2; i <= 20; i++)
            for (int j = 2; j <= 20; j++) {
                if (now[i][j].getPlayer() == Player.NONE) continue;
                this.add(now[i][j]);
                now[i][j].setBounds(now[i][j].getX() * 20 - 10, now[i][j].getY() * 20 - 10, 20, 20);
            }
    }

    public void addPoint(int x, int y) {
        backupBorder();
        Point point = null;
        int a = (x + 10) / 20, b = (y + 10) / 20;
        if (nowPlayer == Player.BLACK) {
            point = new Point(Player.BLACK, a, b);
        } else if (nowPlayer == Player.WHITE) {
            point = new Point(Player.WHITE, a, b);
        } else {
            return;
        }
        if (x / 20 < 2 || y / 20 < 2 || x / 20 > 19 || y / 20 > 19) {
        } else {
            if (nowPlayer == Player.WHITE) {
                this.add(point);
                point.setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
            } else if (nowPlayer == Player.BLACK) {
                this.add(point);
                point.setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
            }
            now[a][b] = point;
            pointsHistory.push(now);
            Begin.write(UpdateMessages.ADD_POINT);
            Begin.write(point);
            isYourTurn = false;
            Begin.label.setText("等待对方下棋。。。");
        }
    }

    public void readMessage() {
        Object temp;
        while ((temp = Begin.read()) != null) {
            UpdateMessages msg = (UpdateMessages) temp;
            switch (msg) {
                case ADD_POINT:
                    temp = Begin.read();
                    Point point = (Point) temp;
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
                        Begin.write(UpdateMessages.MOVE_CONFIRM);
                        System.out.println("发送我同意对方悔棋");
                    } else System.out.println("我不同意对方悔棋");
                    break;
                case MOVE_CONFIRM:
                    JOptionPane.showMessageDialog(null, "对方同意悔棋，现在到你了！");
                    restoreBorder();
                    isYourTurn = true;
                    Begin.label.setText("现在到你下棋了！");
                    break;
                case RECVD_DOUBLEPASS:
                    JOptionPane.showMessageDialog(null, "双方都跳过下棋，比赛结束！\n最终成绩：\n黑：0  白：0\n黑棋赢了！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                    break;
            }
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
        if (e.getModifiersEx() == InputEvent.BUTTON1_DOWN_MASK && isYourTurn) {
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

    public void addPoint(Point point) {
        backupBorder();
        this.add(point);
        now[point.getX()][point.getY()] = point;
    }
}

