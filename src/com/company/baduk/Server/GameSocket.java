package com.company.baduk.Server;

import com.company.baduk.Client.Point;
import com.company.baduk.DataStruct.Player;
import com.company.baduk.DataStruct.PlayerSocket;
import com.company.baduk.DataStruct.UpdateMessages;

import java.io.IOException;

public class GameSocket extends Thread {
    Player nowPlayer;
    PlayerSocket thisPlayer;
    PlayerSocket anotherPlayer;
    Room room;

    public GameSocket(Player nowPlayer, PlayerSocket thisPlayer, PlayerSocket anotherPlayer, Room room) {
        this.room = room;
        this.thisPlayer = thisPlayer;
        this.nowPlayer = nowPlayer;
        this.anotherPlayer = anotherPlayer;
    }

    @Override
    public void run() {
        Object temp;
        System.out.println("准备开始");
        System.out.println("确认身份");
        try {
            while ((temp = thisPlayer.read()) != (null)) {
                UpdateMessages msg = (UpdateMessages) temp;
                System.out.println("读取请求成功");
                switch (msg) {
                    case ADD_POINT:
                        temp = thisPlayer.read();
                        Point point = (Point) temp;
                        anotherPlayer.write(msg);
                        anotherPlayer.write(point);
                        if (room.getFinished() != 0) room.setFinished(0);
                        break;
                    case CLIENT_GIVE_UP:
                        UpdateMessages give_up = UpdateMessages.CLIENT_GIVE_UP;
                        anotherPlayer.write(give_up);
                        break;
                    case CLIENT_PASS:
                        room.startFinished();
                        if (room.getFinished() == 2) {
                            anotherPlayer.write(UpdateMessages.GAME_FINISH);
                            thisPlayer.write(UpdateMessages.GAME_FINISH);
                        } else
                            anotherPlayer.write(UpdateMessages.CLIENT_PASS);
                        break;
                    case RECVD_MOVE:
                        anotherPlayer.write(UpdateMessages.RECVD_MOVE);
                        break;
                    case AGREE_RECVD:
                        anotherPlayer.write(UpdateMessages.AGREE_RECVD);
                        break;
                    case GAME_START:
                        break;
                    default:
                        anotherPlayer.write(msg);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (thisPlayer.getSocket() != null) {
                try {
                    thisPlayer.getSocket().close();
                    for (int i = 0; i < RoomManager.rooms.size(); i++) {
                        if (RoomManager.rooms.get(i).getBlackPlayer().getSocket() == thisPlayer.getSocket()
                                || RoomManager.rooms.get(i).getWhitePlayer().getSocket() == thisPlayer.getSocket()) {
                            RoomManager.rooms.remove(i);
                            break;
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            this.stop();
        }
    }
}
