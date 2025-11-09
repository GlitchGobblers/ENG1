package io.github.some_example_name;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

import java.util.Objects;

public class EventManager {
    MapObjects Interactables;
    MapObjects Barriers;
    boolean event1 = false;
    public EventManager(MapObjects Interactables, MapObjects Barriers) {
        this.Interactables = Interactables;
        this.Barriers = Barriers;
    }
    public String event(int Index, Player player){
        MapObject Event = Interactables.get(Index);
        MapProperties EventProperties = Event.getProperties();
        if (EventProperties.get("Hidden") != null) {
            String data = (String)EventProperties.get("Hidden");
            if (Objects.equals(data, "Event1")){
                event1 = true;
            }
            return data;
        }
        if (EventProperties.get("Event") != null){
            if (Objects.equals((String) EventProperties.get("Event"), "StatChange")){
                float statchange = Float.parseFloat((String)EventProperties.get("StatChange"));
                player.changeSpeed(statchange);
                Interactables.remove(Index);
            }
            //uncomment when victory screen is implemented
            if(Objects.equals((String) EventProperties.get("Event"),  "Win")){
                return "Win";
            }
        }
        return null;
    }
    public boolean getBarriers(){
        return event1;
    }
    private float getProperty(int index, String parameter){
        MapObject temp1 = Barriers.get(index);
        MapProperties temp2 = temp1.getProperties();
        return (float) temp2.get(parameter);

    }

    public com.badlogic.gdx.math.Rectangle getBarrierProperties(int index){

        float x = getProperty(index, "x")/16;
        float y = getProperty(index, "y")/16;
        float width = getProperty(index, "width")/16;
        float height = getProperty(index, "height")/16;
        return new com.badlogic.gdx.math.Rectangle(x, y, width, height);
    }
}
