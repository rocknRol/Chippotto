package rocknrol.project.chippotto.lib.chip8;

public class Timer {

    private long timestamp;

    public void startTimer() {
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExceedTimer(long deltaTime) {
        if(System.currentTimeMillis() - this.timestamp <= deltaTime)
            return false;
        return true;
    }
}
