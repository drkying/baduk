package com.company.baduk.DataStruct;


import com.company.baduk.Client.ChessPad;
import com.company.baduk.Client.Point;

public class GoRules {
    ChessPad chessPad;
    public int[] block;
    int blockLength = 0;

    public GoRules(ChessPad chessPad) {
        this.chessPad = chessPad;
    }

    //使用FloodFill算法找到特定棋子所在的棋子块
    public void FloodFill(int i, int j, Point[][] temp) {
        //Left
        if (i - 1 >= 2 && temp[i - 1][j].getPlayer() == temp[i][j].getPlayer() && !isInBlock((i - 1) * 100 + j)) {
            block[blockLength++] = (i - 1) * 100 + j;
            blockLength++;
            FloodFill(i - 1, j, temp);
        }
        //Up
        if (j - 1 >= 2 && temp[i][j - 1].getPlayer() == temp[i][j].getPlayer() && !isInBlock(i * 100 + j - 1)) {
            block[blockLength] = i * 100 + j - 1;
            blockLength++;
            FloodFill(i, j - 1, temp);
        }
        //Right
        if (i + 1 < 21 && temp[i + 1][j].getPlayer() == temp[i][j].getPlayer() && !isInBlock((i + 1) * 100 + j)) {
            block[blockLength] = (i + 1) * 100 + j;
            blockLength++;
            FloodFill(i + 1, j, temp);
        }
        //Down
        if (j + 1 < 21 && temp[i][j + 1].getPlayer() == temp[i][j].getPlayer() && !isInBlock(i * 100 + j + 1)) {
            block[blockLength] = i * 100 + j + 1;
            blockLength++;
            FloodFill(i, j + 1, temp);
        }
    }

    public boolean isInBlock(int neighbor) {
        for (int i = 0; i < blockLength; i++) {
            if (block[i] == neighbor) return true;
        }
        return false;
    }

    //判断当前下子是否有吃子的情况
    public void checkDelete(int x, int y, Point[][] temp) {
        if (x - 1 >= 2) doStep(x - 1, y, temp);
        if (y - 1 >= 2) doStep(x, y - 1, temp);
        if (x + 1 <= 20) doStep(x + 1, y, temp);
        if (y + 1 <= 20) doStep(x, y + 1, temp);
        doStep(x, y, temp);
    }

    public void doStep(int x, int y, Point[][] temp) {
        if (temp[x][y].getPlayer() == Player.NONE) {
        } else {
            block = new int[361];
            blockLength = 1;
            block[0] = x * 100 + y;

            FloodFill(x, y, temp);

            if (hasQi(temp)) {
            } else {
                //进行吃子
                for (int t = 0; t < blockLength; t++)
                    chessPad.removePoint(temp[block[t] / 100][block[t] % 100], temp);
            }
        }
    }

    //判断当前棋子块是否有气，即未被完全包围
    public boolean hasQi(Point[][] temp) {
        int i, j;
        for (int t = 0; t < blockLength; t++) {
            i = block[t] / 100;
            j = block[t] % 100;
            //System.out.println(now[i + 1][j]);
            if (i - 1 >= 2 && temp[i - 1][j].getPlayer() == Player.NONE) return true;
            if (i + 1 < 21 && temp[i + 1][j].getPlayer() == Player.NONE) return true;
            if (j - 1 >= 2 && temp[i][j - 1].getPlayer() == Player.NONE) return true;
            if (j + 1 < 21 && temp[i][j + 1].getPlayer() == Player.NONE) return true;
        }
        return false;
    }

}
