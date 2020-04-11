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
    Room room;
    ObjectInputStream in;
    ObjectOutputStream out;

    public GameSocket(Socket socket, Player player, Socket anotherPlayer, Room room) {
        this.room = room;
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
                        if (room.getFinished() != 0) room.setFinished(0);
                        break;
                    case CLIENT_GIVE_UP:
                        UpdateMessages give_up = UpdateMessages.CLIENT_GIVE_UP;
                        out(give_up);
                        break;
                    case CLIENT_PASS:
                        room.startFinished();
                        if (room.getFinished() == 2) {
                            out(UpdateMessages.RECVD_DOUBLEPASS);
                            new ObjectOutputStream(anotherPlayer.getOutputStream()).writeObject(UpdateMessages.RECVD_DOUBLEPASS);
                        } else
                            out(UpdateMessages.CLIENT_PASS);
                        break;
                    case RECVD_MOVE:
                        out(UpdateMessages.RECVD_MOVE);
                        break;
                    case MOVE_CONFIRM:
                        out(UpdateMessages.MOVE_CONFIRM);
                        break;
                    default:
                        out(msg);
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
