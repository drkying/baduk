package com.company.baduk.Server;

import com.company.baduk.Client.Point;
import com.company.baduk.DataStruct.PlayerSocket;

import java.util.List;

public class Room {
    private PlayerSocket whitePlayer;
    private PlayerSocket blackPlayer;
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

    @Override
    public String toString() {
        return "Room{" +
                "whitePlayer=" + whitePlayer +
                ", blackPlayer=" + blackPlayer +
                ", roomName=" + roomName +
                ", finished=" + finished +
                ", isConnected=" + isConnected +
                '}';
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

    public Room(PlayerSocket blackPlayer, int roomName) {
        this.blackPlayer = blackPlayer;
        this.roomName = roomName;
    }

    public PlayerSocket getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(PlayerSocket whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public PlayerSocket getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(PlayerSocket blackPlayer) {
        this.blackPlayer = blackPlayer;
    }
}
