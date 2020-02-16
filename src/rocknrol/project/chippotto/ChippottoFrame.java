package rocknrol.project.chippotto;

import rocknrol.project.chippotto.lib.chip8.Chip8System;
import rocknrol.project.chippotto.lib.chip8.Timer;

import javax.swing.*;
import java.awt.*;

public class ChippottoFrame  extends JFrame {

    ChippottoFrame(SessionDataStruct sds) {
        super (">>> CHIPPOTTO Emulator <<<");

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        ChippottoPanel chippottoPanel = new ChippottoPanel(sds);

        chippottoPanel.setBackground(sds.bgColor);
        this.add(chippottoPanel);

        if(sds.fullscreen) {
            sds.pixelSize = dim.width/sds.width;
            if(sds.pixelSize > dim.height/sds.height)
                sds.pixelSize = dim.height/sds.height;
            sds.adjH = (dim.width-(sds.pixelSize*sds.width))/2;
            sds.adjV = (dim.height-(sds.pixelSize*sds.height))/2;
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            if (gd.isFullScreenSupported())
                gd.setFullScreenWindow(this);
        } else {
            this.setResizable(false);
            this.pack();
            this.setSize(sds.width * sds.pixelSize + this.getInsets().right + this.getInsets().left, sds.height * sds.pixelSize + this.getInsets().top + this.getInsets().bottom);
            this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
            this.setVisible(true);
        }

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        sds.dimWidth = this.getSize().width;
        sds.dimHeight = this.getSize().height;

        sds.chip8system = new Chip8System();
        sds.chip8system.setDebugInfo(sds.debugSwitch, sds.debugFile, sds.DebugFileAppend); //, sds.dumpFile, sds.dasmFile, sds.traceFile);
        int len = sds.chip8system.loadRom(sds.rom);
        if(len <= 0)
            System.exit(-1);

        PlayBeep soundBeep = new PlayBeep(sds);
        Thread thrSound = new Thread(soundBeep);
        thrSound.start();

        Timer systemTimer = new Timer();
        systemTimer.startTimer();

        Timer ipsTimer = new Timer();
        ipsTimer.startTimer();

        chippottoPanel.initGraphics();

        int ips = 0;
        int deltaTime = 1000/sds.ipsWish;

        while (true) {
            if(systemTimer.isExceedTimer(deltaTime)) {
                sds.chip8system.cpuStep();
                ips++;
                if(sds.chip8system.isReadyGfxMen()) {
                    chippottoPanel.loadGfxMem(sds.chip8system.getGfxMem());
                    chippottoPanel.render();
                }
                systemTimer.startTimer();
            }

            if(ipsTimer.isExceedTimer(1000)) {
                if(ips < sds.ipsWish) {
                    if (deltaTime > 0)
                        deltaTime--;
                }
                else
                    deltaTime++;

                ips = 0;
                ipsTimer.startTimer();
            }
            if(sds.chip8system.playSound())
                soundBeep.startSound();
            else
                soundBeep.stopSound();
        }
    }
}