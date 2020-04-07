package com.company.baduk.Server;

import java.net.Socket;
import java.util.List;

public class Room {
    private Socket whitePlayer;
    private Socket blackPlayer;
    private int roomName;
    private boolean isConnected=false;


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

    private List<String> chessHistory;
    public Room(){

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

    public List<String> getChessHistory() {
        return chessHistory;
    }

    public void setChessHistory(List<String> chessHistory) {
        this.chessHistory = chessHistory;
    }
}
