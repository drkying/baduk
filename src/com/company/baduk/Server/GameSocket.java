package com.company.baduk.Server;

import com.company.baduk.Client.Point;
import com.company.baduk.DataStruct.Player;

import java.io.*;
import java.net.Socket;

public class GameSocket extends Thread {
    Socket socket;
    Player nowPlayer;
    Socket anotherPlayer;
    ObjectInputStream in;
    ObjectOutputStream out;

    public GameSocket(Socket socket, Player player, Socket anotherPlayer) {
        this.socket = socket;
        this.nowPlayer = player;
        this.anotherPlayer = anotherPlayer;
    }

    public void out(Object msg) {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(anotherPlayer.getOutputStream());
            Object e;
            while ((e = in.readObject()) != (null)) {
                Point point = (Point) e;
                out(point);
                //处理读到的数据
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (socket != null) {
                try {
                    in.close();
                    out.close();
                    socket.close();
                    for (int i = 0; i < RoomManager.rooms.size(); i++) {
                        if (RoomManager.rooms.get(i).getBlackPlayer() == socket) {
                            RoomManager.rooms.remove(i);
                            break;
                        }
                    }
                    this.stop();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
