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
    public String event(int Index, Player player){
        MapObject Event = Interactables.get(Index);
        MapProperties EventProperties = Event.getProperties();
        if (EventProperties.get("Hidden") != null) {
            return (String)EventProperties.get("Hidden");
        }
        if (EventProperties.get("Event") != null){
            if (Objects.equals((String) EventProperties.get("Event"), "StatChange")){
                float statchange = Float.parseFloat((String)EventProperties.get("StatChange"));
                player.changeSpeed(statchange);
                Interactables.remove(Index);
            }
            //uncomment when victory screen is implemented
            //if(Objects.equals((String) EventProperties.get("Event"),  "Win")){
                //call victory screen
            //}
        }
        return null;
    }
}
