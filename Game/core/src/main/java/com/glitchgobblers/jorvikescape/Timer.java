package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.utils.TimeUtils;


/**
 * A simple timer for ensuring the player doesn't take too long.
 */
public class Timer {
    private final int duration;
    private int elapsed;
    private Boolean running;
    private long start;

    public Timer(int duration) {
        this.duration = duration;
        this.running = false;
        this.elapsed = 0;
    }

    public void startTimer() {
        start = TimeUtils.millis();
        elapsed = 0;
        running = true;
    }

    /**
     * Preserves remaining time, so resume continues from where it left off.
     */
    public void pauseTimer() {
        if (running) {
            elapsed += (int) (TimeUtils.timeSinceMillis(start) / 1000);
            this.running = false;
        }
    }

    public void resumeTimer() {
        if (!running) {
            this.start = TimeUtils.millis();
            this.running = true;
        }
    }


    public Boolean getRunning() {
        return running;
    }

    private int getDuration() {
        return duration;
    }

    /**
     *
     * @return time elapsed, in seconds
     */
    private int getElapsed() {
        if (!running) {
            return this.elapsed;
        }

        return this.elapsed + (int) (TimeUtils.timeSinceMillis(start) / 1000);
    }

    public int getTimeLeft() {
        return getDuration() - getElapsed();
    }

    /**
     * @return a string to display in format MM:SS
     */
    public String displayTimer() {
        int currentSeconds = getTimeLeft();
        if (currentSeconds < 0) {
            currentSeconds = 0;
        }

        int minutes = currentSeconds / 60;
        int secs = currentSeconds % 60;

        return String.format("%d:%02d", minutes, secs);
    }
}
