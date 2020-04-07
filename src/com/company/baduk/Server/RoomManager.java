package com.company.baduk.Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class RoomManager {
    public static List<Room> rooms;

    public RoomManager() {
        try {
            int serverPort = 8000;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            while (true){
                Socket s=serverSocket.accept();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
