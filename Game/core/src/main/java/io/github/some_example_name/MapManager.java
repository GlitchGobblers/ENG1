package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import static java.lang.Math.floor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import jdk.internal.org.jline.terminal.impl.LineDisciplineTerminal;

//Main game class, will manage the camera and will store information about the map

public class MapManager implements Screen {

    private Game game;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private OrthographicCamera camera;
    private TiledMapTileLayer roadLayer;
    private SpriteBatch batch;
    private MapObjects Interactables;
    private EventManager EM;
    // Temporary code so that it will show whichever tilemap is in the file location, will have to move to render once things are moving
    public MapManager(Game game, String mapFile){
        this.game = game;
        TiledMap map = new TmxMapLoader().load(mapFile);// this file is a temporary one to see if the renderer is working, its not our final one
        this.roadLayer=(TiledMapTileLayer) map.getLayers().get("Road"); // out of all layers this is safe layer which the player can move on it.
        Interactables = map.getLayers().get("Interactables").getObjects();//Gets all the interactables on the object layer
        EM = new EventManager(Interactables);// Creates a Event manager which handles getting information about the events on the map
        float unitScale = 1/16f;// 1 world unit ==16pixels
        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false,30,20); // temporary size to test, once developed slightly the user may be able to select the size of the game window in the main menu
        this.renderer.setView(camera);
        this.batch = new SpriteBatch();
        player = new Player(new Vector2(12,15));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        this.renderer.setView(camera);
        this.renderer.render();
        batch.setProjectionMatrix(camera.combined);// this ensures that player sprite use the same world units as the map.
        batch.begin();
        player.draw(batch);
        batch.end();
        inputHandler();

    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
    public boolean isTileSafe(float x, float y){
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        float epsilon = 0.01f; // Prevents snagging on exact tile edges


        boolean bottomLeft = IsAreaSafe(x, y);
        boolean bottomRight = IsAreaSafe(x + playerWidth - epsilon, y);
        boolean topLeft = IsAreaSafe(x, y + playerHeight - epsilon);
        boolean topRight = IsAreaSafe(x + playerWidth - epsilon, y + playerHeight - epsilon);

        // Safe only if ALL corners are on a road tile
        return bottomLeft && bottomRight && topLeft && topRight;
    }
    public boolean IsAreaSafe(float x,float y) { // checks whether the tile at the given position is safe to move onto.
        int col = (int) floor( x);
        int row = (int) floor(y);

        if (roadLayer == null || col < 0 || row < 0 || col >= roadLayer.getWidth() || row >= roadLayer.getHeight()) {
            return false;
        } // the statement ensures the player is not moving outside the map boundaries.

        TiledMapTileLayer.Cell cell = roadLayer.getCell(col, row);
        return cell != null && cell.getTile() != null;
        //the getcell() will returns null if there is no tile on the roadlayer at this position
    }

    public void inputHandler(){
        float delta = Gdx.graphics.getDeltaTime();
        float speed = player.getSpeed();

        Vector2 CurrentPosition = player.getPlayerPosition();
        float NewPositionX = CurrentPosition.x;
        float NewPositionY = CurrentPosition.y;

        // Calculate the New movements based on input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            NewPositionX += speed * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            NewPositionX -= speed * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            NewPositionY += speed * delta; }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            NewPositionY -= speed * delta; }

        // collisions check by Separating axes
        // check x-axes
        if (isTileSafe(NewPositionX, CurrentPosition.y)) {
            player.move(NewPositionX, CurrentPosition.y);
        } else {
            NewPositionX = CurrentPosition.x; // use current x if move failed
        }

        //check y-axes
        if (isTileSafe(CurrentPosition.x, NewPositionY)) {
            player.move(CurrentPosition.x, NewPositionY);
        }
        //object handling
        for (int i = 0; i<Interactables.getCount(); i++){
            MapObject Object = Interactables.get(i);
            MapProperties ObjectProperties = Object.getProperties();
            Vector2 ObjectPosition = new Vector2((Float) ObjectProperties.get("x")/16, (Float)ObjectProperties.get("y")/16);
            Vector2 ObjectSize = new Vector2((float)ObjectProperties.get("width")/16, (float) ObjectProperties.get("height")/16);
            if (CurrentPosition.x >= (ObjectPosition.x) && CurrentPosition.x <= ((ObjectPosition.x + ObjectSize.x)) && // Checks if the player is inside the object area
                CurrentPosition.y >= (ObjectPosition.y) && CurrentPosition.y <= ((ObjectPosition.y + ObjectSize.y))){
                if (Gdx.input.isKeyPressed(Input.Keys.E)){
                    String change = EM.event(i);
                    if (Float.parseFloat(change) <= 10){
                        player.setSpeed((float)Integer.parseInt(change));
                    }
                }
            }
        }
    }

}
