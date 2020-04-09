package com.company.baduk.Server;

import com.company.baduk.Client.Point;
import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.UpdateMessages;

import java.io.*;
import java.net.Socket;

public class GameSocket extends Thread {
    Socket socket;
    Player nowPlayer;
    Socket anotherPlayer;
    int finished;
    ObjectInputStream in;
    ObjectOutputStream out;

    public GameSocket(Socket socket, Player player, Socket anotherPlayer) {
        finished = 0;
        this.socket = socket;
        this.nowPlayer = player;
        this.anotherPlayer = anotherPlayer;
        try {
            out = new ObjectOutputStream(anotherPlayer.getOutputStream());
            out.writeObject(nowPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            if (in == null)
                in = new ObjectInputStream(socket.getInputStream());
            if (out == null)
                out = new ObjectOutputStream(anotherPlayer.getOutputStream());
            Object e;
            while ((e = in.readObject()) != (null)) {
                UpdateMessages msg = (UpdateMessages) e;
                switch (msg) {
                    case ADD_POINT:
                        e = in.readObject();
                        Point point = (Point) e;
                        out(msg);
                        out(point);
                        if (finished != 0) finished = 0;
                        break;
                    case CLIENT_GIVE_UP:
                        UpdateMessages give_up = UpdateMessages.CLIENT_GIVE_UP;
                        out(give_up);
                        break;
                    case CLIENT_PASS:
                        finished++;
                        if (finished == 2) {
                            out(UpdateMessages.RECVD_DOUBLEPASS);
                        }
                        break;
                    case RECVD_MOVE:
                        out(UpdateMessages.RECVD_MOVE);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (socket != null) {
                try {
                    if (in != null)
                        in.close();
                    if (out != null)
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
