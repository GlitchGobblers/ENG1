package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

import java.util.Objects;

public class EventManager {
    MapObjects interactables;

    public EventManager(MapObjects Interactables) {
        this.interactables = Interactables;
    }

    public String event(int Index){
        MapObject Event = interactables.get(Index);
        MapProperties EventProperties = Event.getProperties();

        if (EventProperties.get("Map") != null) {
            return (String) EventProperties.get("Map");
        }

        if (EventProperties.get("Event") != null){
            if (Objects.equals(EventProperties.get("Event"), "StatChange")) {
                return (String) EventProperties.get("StatChange");
            }
        }

        return null;
    }
}
