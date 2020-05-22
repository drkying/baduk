package com.company.baduk.DataStruct;


import com.company.baduk.Client.ChessPad;
import com.company.baduk.Client.Point;

public class GoRules {
    ChessPad chessPad;
    Point[][] now;
    public int[] block;
    int blockLength = 0;

    public GoRules(ChessPad chessPad) {
        this.chessPad = chessPad;
        now = chessPad.now;
    }

    public void FloodFill(int i, int j) {
        //Left
        if (i - 1 >= 2 && now[i - 1][j].getPlayer() == now[i][j].getPlayer() && !isInBlock((i - 1) * 100 + j)) {
            block[blockLength++] = (i - 1) * 100 + j;
            blockLength++;
            FloodFill(i - 1, j);
        }
        //Up
        if (j - 1 >= 2 && now[i][j - 1].getPlayer() == now[i][j].getPlayer() && !isInBlock(i * 100 + j - 1)) {
            block[blockLength] = i * 100 + j - 1;
            blockLength++;
            FloodFill(i, j - 1);
        }
        //Right
        if (i + 1 < 21 && now[i + 1][j].getPlayer() == now[i][j].getPlayer() && !isInBlock((i + 1) * 100 + j)) {
            block[blockLength] = (i + 1) * 100 + j;
            blockLength++;
            FloodFill(i + 1, j);
        }
        //Down
        if (j + 1 < 21 && now[i][j + 1].getPlayer() == now[i][j].getPlayer() && !isInBlock(i * 100 + j + 1)) {
            block[blockLength] = i * 100 + j + 1;
            blockLength++;
            FloodFill(i, j + 1);
        }
    }

    public boolean isInBlock(int neighbor) {
        for (int i = 0; i < blockLength; i++) {
            if (block[i] == neighbor) return true;
        }
        return false;
    }

    public void checkDelete(int x, int y) {
        if (x - 1 >= 2) doStep(x - 1, y);
        if (y - 1 >= 2) doStep(x, y - 1);
        if (x + 1 <= 20) doStep(x + 1, y);
        if (y + 1 <= 20) doStep(x, y + 1);
        doStep(x, y);
    }

    public void doStep(int x, int y) {
        if (now[x][y].getPlayer() == Player.NONE) {
        } else {
            block = new int[361];
            blockLength = 1;
            block[0] = x * 100 + y;

            FloodFill(x, y);

            if (hasQi()) {
            } else {
                for (int t = 0; t < blockLength; t++)
                    chessPad.removePoint(now[block[t] / 100][block[t] % 100]);
            }
        }
    }

    public boolean hasQi() {
        int i, j;
        for (int t = 0; t < blockLength; t++) {
            i = block[t] / 100;
            j = block[t] % 100;
            System.out.println(now[i + 1][j]);
            if (i - 1 >= 2 && now[i - 1][j].getPlayer() == Player.NONE) return true;
            if (i + 1 < 21 && now[i + 1][j].getPlayer() == Player.NONE) return true;
            if (j - 1 >= 2 && now[i][j - 1].getPlayer() == Player.NONE) return true;
            if (j + 1 < 21 && now[i][j + 1].getPlayer() == Player.NONE) return true;
        }
        return false;
    }

}
