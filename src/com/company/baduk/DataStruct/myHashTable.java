package com.company.baduk.DataStruct;

import com.company.baduk.Client.Point;

import java.util.ArrayList;
import java.util.List;

public class myHashTable {
    public List<String> chessHash;
    public List<Point[][]> chessHistory;

    public myHashTable() {
        this.chessHash = new ArrayList<>();
        this.chessHistory = new ArrayList<>();
    }

    public void add(Point[][] temp) {
        chessHistory.add(temp);
        chessHash.add(hashFun(temp));
    }

    //为当前棋局生成一个哈希值
    public static String hashFun(Point[][] points) {
        int hash = 0;
        for (int i = 2; i <= 20; i++) {
            for (int j = 2; j <= 20; j++) {
                Point point = points[i][j];
                if (point.getPlayer().equals(Player.BLACK))
                    hash += (point.getX() * 3 + point.getY() * 5) * 3;
                else if (point.getPlayer().equals(Player.WHITE))
                    hash += (point.getX() * 3 + point.getY() * 5) * 5;
                else if (point.getPlayer().equals(Player.NONE))
                    hash--;
            }
        }
        return String.valueOf(hash);
    }

    public int find(String key) {
        return chessHash.indexOf(key);
    }

    public Point[][] get(int i) {
        return chessHistory.get(i);
    }

    public Point[][] pop() {
        chessHistory.remove(chessHistory.size() - 1);
        chessHash.remove(chessHash.size() - 1);
        return chessHistory.get(chessHistory.size() - 1);
    }

}
