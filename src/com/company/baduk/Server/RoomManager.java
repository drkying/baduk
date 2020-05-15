package com.company.baduk.Server;

import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.PlayerSocket;

import java.io.DataInputStream;
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
                PlayerSocket playerSocket = new PlayerSocket(serverSocket.accept());
                System.out.println("接收到一个请求");
                int x = 0;
                try {
                    x = playerSocket.readInt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int i;
                for (i = 0; i < rooms.size(); i++) {
                    Room e = rooms.get(i);
                    System.out.println("finding");
                    System.out.println(e);
                    if (e.getRoomName() == x) {
                        e.setWhitePlayer(playerSocket);
                        System.out.println("found\n" + e);
                        e.getBlackPlayer().write(Player.BLACK);
                        e.getWhitePlayer().write(Player.WHITE);
                        new GameSocket(Player.BLACK, e.getBlackPlayer(), e.getWhitePlayer(), e).start();
                        new GameSocket(Player.WHITE, e.getWhitePlayer(), e.getBlackPlayer(), e).start();
                        break;
                    }
                }
                if (i == rooms.size()) {
                    Room e = new Room(playerSocket, x);
                    rooms.add(e);
                    System.out.println("not found\n" + e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
