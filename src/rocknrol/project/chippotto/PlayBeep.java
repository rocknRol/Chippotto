package rocknrol.project.chippotto;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.lang.Runnable;

public class PlayBeep implements Runnable {

    SessionDataStruct sds;
    boolean playSound = false;

    public PlayBeep(SessionDataStruct sds) {
        this.sds = sds;
    }

    @Override
    public void run() {
        float sampleRate = 8000f;
        final int BUFSIZE = 8000;
        byte[] buf = new byte[BUFSIZE];
        byte[] b = new byte[1];
        AudioFormat af = new AudioFormat(sampleRate, 8, 1, true, false);

        double angle = 0;
        for(int i=0;i < BUFSIZE; i++) {
            angle = i / (sampleRate / sds.Hz) * 2.0 * Math.PI;
            buf[i] = (byte) (Math.sin(angle) * 127.0);
        }

        try {
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af);
            sdl.start();

            for(int i=0;i<5000;i++) {
                b[0] = 0;
                sdl.write(b, 0, 1);
            }
            sdl.drain();

            while(true) {
                int i = 0;
                while (playSound) {
                    b[0] = buf[i];
                    sdl.write(b, 0, 1);
                    if(i >= BUFSIZE-1)
                        i=0;
                    else
                        i++;
                }
                sdl.drain();
            }
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }
    }

    public void startSound() {
        playSound = true;
    }

    public void stopSound() {
        playSound = false;
    }

}
