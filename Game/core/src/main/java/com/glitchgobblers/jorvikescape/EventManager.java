package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;

import java.util.Objects;

public class EventManager {
    MapObjects interactables;
    MapObjects barriers;
    boolean event1 = false;
    int event2 = 0;
    int eventScore = 0;

    public EventManager(MapObjects interactables, MapObjects barriers) {
        this.interactables = interactables;
        this.barriers = barriers;
    }

    public String event(int index, Player player) {
        MapObject Event = interactables.get(index);
        MapProperties EventProperties = Event.getProperties();

        if (EventProperties.get("Hidden") != null) {
            String data = (String) EventProperties.get("Hidden");
            if (Objects.equals(data, "Hidden1")) {
                event1 = true;
                interactables.remove(index);
                toggleBarrier(0);
                eventScore += 1;
            }

            if (Objects.equals(data, "Hidden2")) {
                event2 += 1;
                interactables.remove(index);
                eventScore += 1;
            }

            return data;
        }

        if (EventProperties.get("Event") != null) {
            if (Objects.equals(EventProperties.get("Event"), "StatChange")) {
                float statchange = Float.parseFloat((String) EventProperties.get("StatChange"));
                player.changeSpeed(statchange);
                interactables.remove(index);
                eventScore += 1;
            }

            if (Objects.equals(EventProperties.get("Event"), "Win")) {
                return "Win";
            }
        }

        return null;
    }

    public boolean getBarriers() {
        return event1;
    }

    private <T> T getProperty(int index, String parameter) {
        MapObject temp1 = barriers.get(index);
        MapProperties temp2 = temp1.getProperties();
        return (T) temp2.get(parameter);
    }

    private <T> void setProperties(int index, String parameter1, T parameter2) {
        MapObject temp1 = barriers.get(index);
        MapProperties temp2 = temp1.getProperties();
        temp2.put(parameter1, parameter2);
    }

    public Rectangle getBarrierProperties(int index) {
        float x = getProperty(index, "x");
        float y = getProperty(index, "y");
        float width = getProperty(index, "width");
        float height = getProperty(index, "height");
        return new Rectangle(x / 16, y / 16, width / 16, height / 16);
    }

    public boolean getStone() {
        toggleBarrier(1);
        return (event2 >= 3);
    }

    public boolean isBarrier(int index) {
        return getProperty(index, "Barrier");
    }

    public void toggleBarrier(int index) {
        setProperties(index, "Barrier", !(boolean) getProperty(index, "Barrier"));
    }

    public int getScore() {
        return eventScore;
    }
}
