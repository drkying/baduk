package com.company.baduk.Client;

import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.myStack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

public class ChessPad extends JPanel implements MouseListener, ActionListener {
    private Player nowPlayer = Player.BLACK;
    private myStack<Point> points;
    Point[][] two = new Point[19][19];
    private LinkedList<Point[][]> pointsList = new LinkedList<>();

    public ChessPad() {
        points = new myStack<>();
        this.setBackground(Color.ORANGE);
        setSize(440, 440);
        addMouseListener(this);
        setLayout(null);
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
        points = new myStack<>();
    }


    public void setBorder(Point[][] points) {
        removePoints();
        for (int i = 1; i <= 19; i++)
            for (int j = 1; j <= 19; j++) {
                addPoint(points[i][j]);
            }
    }

    public void addPoint(Point point) {
        int x = point.getX();
        int y = point.getY();
        int a = (x + 10) / 20, b = (y + 10) / 20;
        if (x / 20 < 2 || y / 20 < 2 || x / 20 > 19 || y / 20 > 19) {
        } else {
            this.add(point);
            point.setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
        }
    }

    public void addPoint(int x, int y) {
        Point point = null;
        int a = (x + 10) / 20, b = (y + 10) / 20;
        if (nowPlayer == Player.BLACK) {
            point = new Point(Player.BLACK, a, b);
        } else if (nowPlayer == Player.WHITE) {
            point = new Point(Player.WHITE, a, b);
        } else {
            System.exit(0);
        }
        if (x / 20 < 2 || y / 20 < 2 || x / 20 > 19 || y / 20 > 19) {
        } else {
            if (nowPlayer == Player.WHITE) {
                this.add(point);
                point.setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
                nowPlayer = Player.BLACK;
            } else if (nowPlayer == Player.BLACK) {
                this.add(point);
                point.setBounds(a * 20 - 10, b * 20 - 10, 20, 20);
                nowPlayer = Player.WHITE;
            }
            points.push(point);
            two[a][b] = point;
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
}

