package com.company.baduk.Server;

import com.company.baduk.Client.Point;

import java.net.Socket;
import java.util.List;

public class Room {
    private Socket whitePlayer;
    private Socket blackPlayer;
    private int roomName;
    public int finished = 0;

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public void startFinished() {
        finished++;
    }


    private boolean isConnected = false;
    private List<Point[][]> chessHistory;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getRoomName() {
        return roomName;
    }

    public void setRoomName(int roomName) {
        this.roomName = roomName;
    }


    public Room() {
    }

    public Room(Socket blackPlayer, int roomName) {
        this.blackPlayer = blackPlayer;
        this.roomName = roomName;
    }

    public Socket getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Socket whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Socket getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Socket blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
}
