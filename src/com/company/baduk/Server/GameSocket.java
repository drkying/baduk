package com.company.baduk.Server;

import com.company.baduk.DataStruct.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class GameSocket extends Thread {
    Socket socket;
    Player nowPlayer;

    public GameSocket(Socket socket, Player player) {
        this.socket = socket;
        this.nowPlayer = player;
    }

    public void out(String msg) {
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run(){
        try {
            DataInputStream in=new DataInputStream(socket.getInputStream());
            String line;
            while ((line = in.readUTF()) != (null)){
            //处理读到的数据
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
