package io.github.some_example_name;

import com.badlogic.gdx.utils.TimeUtils;


public class Timer {
    private int seconds = 0;
    private Boolean running;
    private long start;






    public Timer (int minutes) {
        this.seconds = minutes * 60;
        this.running = false;
    }

    public void startTimer() {
        start = TimeUtils.millis();
        running = true;
    }

    public void restartTimer(int minutes) {
        this.seconds = minutes*60;
        startTimer();
    }
    public void stopTimer() {
        // use this for pause menu
            running = false;
    }

    public Boolean getRunning() {
        return running;
    }

    public int getSeconds() {
        if (running == false) {
            return seconds;
        }
        int currentDuration = (int) (TimeUtils.timeSinceMillis(start) / 1000);
        int currentseconds = seconds - currentDuration;
        return currentseconds;
    }

    public String displayTimer()
    {
        int currentSeconds = getSeconds();
        if (currentSeconds < 0) {
            currentSeconds = 0;
        }
        int minutes = currentSeconds/60;
        int secs = currentSeconds%60;

        String time = String.format ("%d:%02d",minutes,secs);
        return time;
    }
}
