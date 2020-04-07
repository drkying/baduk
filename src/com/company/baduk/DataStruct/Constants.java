package com.company.baduk.DataStruct;

public class Constants {
    public static final int BOARD_DIM = 9;
    public static final int SEND_PASS = 253; //large enough to safely fit 19x19 board positions in recv (see Model.receive)
    public static final int SEND_DOUBLEPASS = 254;
    public static final int SEND_QUIT = 255;
}
