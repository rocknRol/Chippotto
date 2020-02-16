package rocknrol.project.chippotto;

import rocknrol.project.chippotto.lib.chip8.Chip8System;

import java.awt.Color;

class SessionDataStruct {

    Chip8System chip8system = null;

    int ipsWish = 250;
    Color pixelColor = Color.GREEN;
    int pixelSize = 8;
    Color bgColor = Color.BLACK;
    String rom;
    boolean fullscreen = false;
    byte debugSwitch = 0x00;
    String debugFile = "";
    boolean DebugFileAppend = false;
    int adjH = 0;
    int adjV = 0;
    int width = 64;
    int height = 32;
    int dimWidth;
    int dimHeight;
    double Hz = 330.0; //musical note: E4 (MI4)
}
