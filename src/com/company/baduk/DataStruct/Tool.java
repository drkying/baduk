package com.company.baduk.DataStruct;

import com.company.baduk.Client.Point;

public class Tool {
    public static boolean ipCheck(String text) {
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
}
