package io.github.some_example_name;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;

import java.util.Objects;

public class EventManager {
    MapObjects Interactables;
    MapObjects Barriers;
    boolean event1 = false;
    int event2 = 0;
    public EventManager(MapObjects Interactables, MapObjects Barriers) {
        this.Interactables = Interactables;
        this.Barriers = Barriers;
    }
    public String event(int Index, Player player){
        MapObject Event = Interactables.get(Index);
        MapProperties EventProperties = Event.getProperties();
        if (EventProperties.get("Hidden") != null) {
            String data = (String)EventProperties.get("Hidden");
            if (Objects.equals(data, "Hidden1")){
                event1 = true;
                Interactables.remove(Index);
                toggleBarrier(0);

            }
            if (Objects.equals(data, "Hidden2")){
                event2 +=1;
                Interactables.remove(Index);
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
    private <T> T getProperty(int index, String parameter){
        MapObject temp1 = Barriers.get(index);
        MapProperties temp2 = temp1.getProperties();
        return (T) temp2.get(parameter);

    }
    private <T> void setProperties(int index, String parameter1, T parameter2){
        MapObject temp1 = Barriers.get(index);
        MapProperties temp2 = temp1.getProperties();
        temp2.put(parameter1 , parameter2);
    }

    public com.badlogic.gdx.math.Rectangle getBarrierProperties(int index){

        float x = getProperty( index, "x");
        float y = getProperty(index, "y");
        float width = getProperty(index, "width");
        float height = getProperty(index, "height");
        return new com.badlogic.gdx.math.Rectangle(x/16, y/16, width/16, height/16);
    }
    public boolean getStone(){
        toggleBarrier(1);
        return (event2 >= 3);
    }
    public boolean isBarrier(int Index){
        return getProperty(Index, "Barrier");
    }
    public void toggleBarrier(int Index){
        if (getProperty(Index, "Barrier")){
            setProperties(Index, "Barrier", false);
        }
        else{
            setProperties(Index, "Barrier", true);
        }
    }
}
