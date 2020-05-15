package com.company.baduk.Client;

import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.PlayerSocket;
import com.company.baduk.DataStruct.Tool;
import com.company.baduk.DataStruct.UpdateMessages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Begin extends JFrame {
    ChessPad chesspad;
    PlayerSocket playerSocket;
    static JTextArea score = new JTextArea();
    JButton pass = new JButton("跳过");
    JButton give_up = new JButton("认输");
    JButton undo = new JButton("悔棋");
    static JLabel label;


    public Begin() {
        String ip, roomName;
//        ip = JOptionPane.showInputDialog("请输入服务器ip:");
//        while (!Tool.ipCheck(ip)){
//            JOptionPane.showMessageDialog(null, "输入的ip不合法,请重试");
//            ip = JOptionPane.showInputDialog("请输入服务器ip:");
//        }
//        roomName = JOptionPane.showInputDialog("请输入房间号:");

        ip = "127.0.0.1";
        roomName = "233";
        request(roomName, ip);
        start();
    }

    public void request(String roomName, String ip) {
        int x = Tool.encode(roomName);
        System.out.println("after encode:\n" + x);
        try {
            playerSocket = new PlayerSocket(new Socket(ip, 8000));
            playerSocket.writeInt(x);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("request finish");
    }

    public void init() {
        pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chesspad.isYourTurn()) {
                    int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否跳过?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                    if (n == 0) {
                        playerSocket.write(UpdateMessages.CLIENT_PASS);
                        System.out.println("我发送了跳过");
                        //发送跳过
                        chesspad.setYourTurn(false);
                        label.setText("等待对方下棋。。。");
                    }
                }
            }
        });
        give_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否认输?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                if (n == 0) {
                    System.out.println("我发送了认输");
                    playerSocket.write(UpdateMessages.CLIENT_GIVE_UP);
                    JOptionPane.showMessageDialog(null, "你认输，游戏结束！");
                    System.exit(0);
                }
                //发送认输
            }
        });
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!chesspad.isYourTurn()) {
                    int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否悔棋?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                    if (n == 0) {
                        System.out.println("我发送了悔棋");
                        playerSocket.write(UpdateMessages.RECVD_MOVE);
                    }
                }
            }
        });
        score.setEditable(false);
        score.setBackground(null);
        score.setText("计分板：" + "黑：0 " + "白： 0");
    }

    public void start() {
        label = new JLabel("现在是黑子下棋", JLabel.CENTER);
        playerSocket.write(UpdateMessages.GAME_START);
        System.out.println("准备开始");
        Player temp = null;
        try {
            temp = (Player) playerSocket.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("确认身份");
        chesspad = new ChessPad(temp, playerSocket);
        init();
        setTitle("围棋");
        setVisible(true);
        setLayout(null);
        add(chesspad);
        add(give_up);
        add(pass);
        add(undo);
        add(score);
        add(label);
        label.setBackground(Color.orange);
        label.setBounds(130, 55, 320, 26);

        chesspad.setBounds(70, 90, 440, 440);
        give_up.setBounds(70, 55, 60, 26);
        pass.setBounds(450, 55, 60, 26);
        undo.setBounds(450, 25, 60, 26);
        score.setBounds(70, 25, 380, 26);


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
