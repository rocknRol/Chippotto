package rocknrol.project.chippotto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class ChippottoPanel extends JPanel {

    SessionDataStruct sds;

    byte[] gfxMem = new byte[256];

    Image offscreen;
    Graphics offgc;

    HashMap<String, Byte> keyboardMap = new HashMap<String, Byte>();

    String[] pcKeyboard = {"1", "2", "3", "4",
                           "Q", "W", "E", "R",
                           "A", "S", "D", "F",
                           "Z", "X", "C", "V"};

    Byte[] chip8Keyboard = {0x01, 0x02, 0x03, 0x0c,
                            0x04, 0x05, 0x06, 0x0d,
                            0x07, 0x08, 0x09, 0x0e,
                            0x0a, 0x00, 0x0b, 0x0f};

    public ChippottoPanel(SessionDataStruct sds) {
        KeyListener listener = new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCodeTmp = e.getKeyCode();
                String keyTextTmp = KeyEvent.getKeyText(keyCodeTmp);

                if(keyCodeTmp == 27)
                    System.exit(0);

                if(keyboardMap.containsKey(keyTextTmp)) {
                    sds.chip8system.setKeyboardValue(keyboardMap.get(keyTextTmp));
                    sds.chip8system.setKeyboardStatus(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                sds.chip8system.setKeyboardStatus(false);
            }
        };

        this.sds = sds;
        addKeyListener(listener);
        setFocusable(true);

        for(int i=0;i<16;i++)
            keyboardMap.put(pcKeyboard[i],chip8Keyboard[i]);

        this.setDoubleBuffered(true);
    }

    public void initGraphics() {
        offscreen = createImage(sds.dimWidth, sds.dimHeight);
        offgc = offscreen.getGraphics();
    }

    public void render() {
        offgc.setColor(sds.bgColor);
        offgc.fillRect(0, 0, sds.dimWidth, sds.dimHeight);
        offgc.setColor(sds.pixelColor);

        for(int i = 0; i < 256; i++) {
            for(int j = 0; j < 8; j++) {
                if(((gfxMem[i] >> j) & 0x01) == 1) {
                    offgc.fillRect(((i%8)*8 + (7-j)) * sds.pixelSize + sds.adjH, (i/8) * sds.pixelSize + sds.adjV, sds.pixelSize, sds.pixelSize);
                }

            }
        }
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(offscreen, 0, 0, this);
    }

    public void loadGfxMem(byte[] gfxMem) {
        this.gfxMem = gfxMem;
    }

}