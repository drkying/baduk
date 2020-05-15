package com.company.baduk.DataStruct;

import java.io.*;
import java.net.Socket;

public class PlayerSocket {
    Socket socket;
    ObjectInputStream objectInputStream = null;
    ObjectOutputStream objectOutputStream = null;
    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    public PlayerSocket() {
        socket = null;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public PlayerSocket(Socket socket) {
        this.socket = socket;
    }

    public void writeInt(int msg) {
        try {
            if (dataOutputStream == null) dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readInt() throws Exception {
        int temp = -1;
        if (dataInputStream == null) dataInputStream = new DataInputStream(socket.getInputStream());
        if ((temp = dataInputStream.readInt()) != (-1)) {
            return temp;
        }
        return -1;
    }

    public void write(Object msg) {
        try {
            System.out.println("begin writing...\n" + socket.toString() + "\n" + socket.getInputStream().toString());
            if (objectOutputStream == null) objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("writing to...\n" + objectOutputStream.toString());
            objectOutputStream.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object read() throws Exception {
        Object temp = null;
        if (objectInputStream == null) objectInputStream = new ObjectInputStream(socket.getInputStream());
        if ((temp = objectInputStream.readObject()) != (null)) {
            return temp;
        }
        return null;
    }
}
