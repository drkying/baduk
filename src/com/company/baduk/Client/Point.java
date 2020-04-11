package com.company.baduk.Client;

import com.company.baduk.DataStruct.Player;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

public class Point extends Canvas implements MouseListener, Serializable {
    private Player player = Player.NONE;
    private Type type = Type.C;
    private int x, y;
    private int worth;

    public enum Type {
        /**
         * Center
         */
        C,
        /**
         * Bottom edge
         */
        E_B,
        /**
         * Left edge
         */
        E_L,
        /**
         * Right edge
         */
        E_R,
        /**
         * Top edge
         */
        E_T,
        /**
         * Bottom left corner
         */
        CRN_BL,
        /**
         * Bottom right corner
         */
        CRN_BR,
        /**
         * Top left corner
         */
        CRN_TL,
        /**
         * Top right corner
         */
        CRN_TR
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType() {
        if ((x > 2 && x < 20) && (y > 2 && y < 20)) {
            type = Type.C;
        } else if (x == 2 && y == 2) {
            type = Type.CRN_TL;
        } else if (x == 20 && y == 2) {
            type = Type.CRN_TR;
        } else if (x == 2 && y == 20) {
            type = Type.CRN_BL;
        } else if (x == 20 && y == 20) {
            type = Type.CRN_BR;
        } else if (x == 2 && (y > 2 && y < 20)) {
            type = Type.E_T;
        } else if (y == 2 && (x > 2 && x < 20)) {
            type = Type.E_L;
        } else if (x == 20 && (y > 2 && y < 20)) {
            type = Type.E_B;
        } else if (y == 20 && (x > 2 && x < 20)) {
            type = Type.E_R;
        }
    }

    @Override
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point(Player player, int x, int y) {
        this.player = player;
        this.x = x;
        this.y = y;
        setSize(20, 20);
        addMouseListener(this);
        setType();
    }

    public void paint(Graphics g) {
        if (player == Player.BLACK) {
            g.setColor(Color.BLACK);
        } else if (player == Player.WHITE) {
            g.setColor(Color.WHITE);
        }
        g.fillOval(0, 0, 20, 20);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

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
