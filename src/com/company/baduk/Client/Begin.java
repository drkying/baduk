package com.company.baduk.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Begin extends JFrame {
    ChessPad chesspad = new ChessPad();

    public Begin() {
        //chesspad.setBackground(Color.ORANGE);
        setVisible(true);
        setLayout(null);
        add(chesspad);
        chesspad.setBounds(70, 90, 440, 440);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        pack();
        setSize(600, 550);
    }

    public static void main(String args[]) {
        Begin chess = new Begin();
    }
}
