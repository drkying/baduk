package com.company.baduk.Client;

import com.company.baduk.DataStruct.Tool;

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
    ChessPad chesspad = new ChessPad();
    JTextArea score = new JTextArea();
    JButton pass = new JButton("跳过");
    JButton give_up = new JButton("认输");
    JButton undo = new JButton("悔棋");
    static Socket s;
    static InputStream inputStream;
    static OutputStream outputStream;
    static ObjectInputStream objectInputStream;
    static ObjectOutputStream objectOutputStream;


    public Begin() {
        init();
        // String ip = JOptionPane.showInputDialog("请输入服务器ip:");
        // if (ip == null) System.exit(0);
        //if (Tool.ipCheck(ip)) {
        //  String roomName = JOptionPane.showInputDialog("请输入房间号:");
        //  request(roomName);
        request("1");
        System.out.println(4);
        start();

//        } else {
//            JOptionPane.showMessageDialog(null, "输入的ip不合法");
//            System.exit(0);
//        }
    }

    public void request(String roomName) {
        int x = Tool.encode(roomName);
        try {
            initStream();
            outputStream.write(x);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("request finish");
    }

    private void initStream() throws IOException {
        s = new Socket("127.0.0.1", 8000);
        outputStream = s.getOutputStream();
        inputStream = s.getInputStream();
    }

    public static void write(Object point) {
        try {
            if (objectOutputStream == null) objectOutputStream = new ObjectOutputStream(outputStream);

            objectOutputStream.writeObject(point);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object read() {
        Object point = null;
        try {
            if (objectInputStream == null) objectInputStream = new ObjectInputStream(inputStream);
            if ((point = objectInputStream.readObject()) != (null)) {
                return point;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return point;
    }

    public void init() {
        pass.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否跳过?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                if (n == 0)
                    System.out.println("是");
                //发送跳过
            }
        });
        give_up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否认输?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                if (n == 0)
                    System.out.println("是");
                //发送认输
            }
        });
        undo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null, "请确认:\n是否悔棋?", "提示", JOptionPane.YES_NO_OPTION);//i=0/1
                if (n == 0)
                    chesspad.restoreBorder();

            }
        });
        score.setEditable(false);
        score.setBackground(null);
        score.setText("计分板：" + "黑：0 " + "白： 0");
    }

    public void start() {
        setTitle("围棋");
        setVisible(true);
        setLayout(null);
        add(chesspad);
        add(give_up);
        add(pass);
        add(undo);
        add(score);
        JLabel label = new JLabel("现在是黑子下棋", JLabel.CENTER);
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
