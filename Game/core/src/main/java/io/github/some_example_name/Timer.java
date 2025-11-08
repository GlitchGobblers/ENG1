package io.github.some_example_name;

import com.badlogic.gdx.utils.TimeUtils;


/**
 * A simple timer for ensuring the player doesn't take too long.
 */
public class Timer {
    private int seconds;
    private Boolean running;
    private long start;

    public Timer(float minutes) {
        this.seconds = (int) (minutes * 60);
        this.running = false;
    }

    public void startTimer() {
        start = TimeUtils.millis();
        running = true;
    }

    /**
     * Preserves remaining time, so resume continues from where it left off.
     */
    public void pauseTimer() {
        this.seconds = getSeconds();
        this.running = false;
    }

    public Boolean getRunning() {
        return running;
    }

    /**
     * Skips the calculation if the timer is paused, as pausing the timer directly sets the number of seconds as of when
     * it was paused. This is to avoid time after it was paused counting against it.
     * @return the number of seconds left on the clock
     */
    public int getSeconds() {
        if (running == false) {
            return seconds;
        }

        int currentDuration = (int) (TimeUtils.timeSinceMillis(start) / 1000);
        return seconds - currentDuration;
    }

    /**
     * @return a string to display in format MM:SS
     */
    public String displayTimer() {
        int currentSeconds = getSeconds();
        if (currentSeconds < 0) {
            currentSeconds = 0;
        }

        int minutes = currentSeconds / 60;
        int secs = currentSeconds % 60;

        return String.format("%d:%02d", minutes, secs);
    }
}
