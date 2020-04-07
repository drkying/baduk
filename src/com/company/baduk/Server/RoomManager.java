package com.company.baduk.Server;

import com.company.baduk.DataStruct.Player;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    public static List<Room> rooms = new ArrayList<>();

    public RoomManager() {
        try {
            int serverPort = 8000;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true) {
                Socket s = serverSocket.accept();
                InputStream inputStream = s.getInputStream();
                int x = inputStream.read();
                int i;
                for (i = 0; i < rooms.size(); i++) {
                    Room e = rooms.get(i);
                    System.out.println(rooms.size());
                    System.out.println(e);
                    if (e.getRoomName() == x) {
                        e.setWhitePlayer(s);
                        System.out.println("666" + e);
                        new GameSocket(e.getBlackPlayer(), Player.BLACK, e.getWhitePlayer()).start();
                        new GameSocket(e.getWhitePlayer(), Player.WHITE, e.getBlackPlayer()).start();
                        break;
                    }
                }
                if (i == rooms.size()) {
                    Room e = new Room(s, x);
                    rooms.add(e);
                    System.out.println(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
