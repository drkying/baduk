package com.company.baduk.DataStruct;

import com.company.baduk.Client.Point;

public class Tool {
    //判断IP是否合法
    public static boolean ipCheck(String text) {
        if (text.equals("localhost"))
            return true;
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

    public static int encode(String roomName) {
        int s = 0;
        for (int i = 0; i < roomName.length(); i++) {
            s += (int) roomName.charAt(i);
        }
        return s;
    }

    public boolean isDead(Point[][] points, int x, int y) {
        if (points[x + 1][y].getPlayer() == Player.NONE
                || points[x][y + 1].getPlayer() == Player.NONE
                || points[x - 1][y].getPlayer() == Player.NONE
                || points[x][y - 1].getPlayer() == Player.NONE)
            return false;
        return true;
    }

    public static String sumHashCode(Point[][] points) {
        int hash = 0;
        for (int i = 2; i <= 20; i++) {
            for (int j = 2; j <= 20; j++) {
                Point point = points[i][j];
                if (point.getPlayer().equals(Player.BLACK))
                    hash += (point.getX() * 3 + point.getY() * 5) * 7;
                else if (point.getPlayer().equals(Player.WHITE))
                    hash += (point.getX() * 3 + point.getY() * 5) * 9;
                else hash--;
            }
        }
        return String.valueOf(hash);
    }
}
