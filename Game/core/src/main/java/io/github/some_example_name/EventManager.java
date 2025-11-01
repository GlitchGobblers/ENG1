package io.github.some_example_name;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

import java.util.Objects;

public class EventManager {
    MapObjects Interactables;
    public EventManager(MapObjects Interactables) {
        this.Interactables = Interactables;
    }
    public String event(int Index){
        MapObject Event = Interactables.get(Index);
        MapProperties EventProperties = Event.getProperties();
        if (EventProperties.get("Map") != null) {
            return (String) EventProperties.get("Map");
        }
        if (EventProperties.get("Event") != null){
            if (Objects.equals((String) EventProperties.get("Event"), "StatChange")){
                return (String)EventProperties.get("StatChange");
            }
        }
        return null;
    }
}
