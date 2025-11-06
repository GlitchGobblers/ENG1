package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

//Main game class, will manage the camera and will store information about the map

public class MapManager implements Screen {
    private Game game;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private OrthographicCamera camera;
    private OrthographicCamera timerCamera;
    private TiledMapTileLayer roadLayer;
    private SpriteBatch batch;
    private MapObjects interactables;
    private EventManager EM;
    private Timer timer;
    private final BitmapFont font;
    private GlyphLayout layout;

    // 1 world unit == 16 pixels
    private final float unitScale = 1/16f;

    // Temporary code so that it will show whichever tilemap is in the file location, will have to move to render once things are moving
    public MapManager(Game game, String mapFile){
        this.game = game;

        // this file is a temporary one to see if the renderer is working, it's not our final one
        TiledMap map = new TmxMapLoader().load(mapFile);
        // out of all layers this is safe layer which the player can move on it.
        this.roadLayer = (TiledMapTileLayer) map.getLayers().get("Road");

        //Gets all the interactables on the object layer
        interactables = map.getLayers().get("Interactables").getObjects();

        // Creates a Event manager which handles getting information about the events on the map
        EM = new EventManager(interactables);

        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        this.camera = new OrthographicCamera();
        camera.setToOrtho(false,60,40); // temporary size to test, once developed slightly the user may be able to select the size of the game window in the main menu

        this.renderer.setView(camera);
        this.batch = new SpriteBatch();

        // we need to keep the unitscale constant so pass it in
        player = new Player(new Vector2(13,36), unitScale);

        // we're using a freetype font system to avoid blurry scaling issues
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        this.font = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
    }

    @Override
    public void show() {
        timer = new Timer(5);
        timer.startTimer();

        layout = new GlyphLayout();

        timerCamera = new OrthographicCamera();
        timerCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.renderer.setView(camera);
        this.renderer.render();

        batch.setProjectionMatrix(camera.combined);// this ensures that player sprite use the same world units as the map.
        batch.begin();

        // <INSIDE BATCH CALL>
        player.draw(batch);
        batch.setProjectionMatrix(timerCamera.combined); // player can see timer

        String timerText = timer.displayTimer();
        layout.setText(font, timerText);

        // appears in top right corner
        float x = Gdx.graphics.getWidth() - layout.width - 20;
        float y = Gdx.graphics.getHeight() - 20;

        font.draw(batch, timerText, x, y);
        // </INSIDE BATCH CALL>

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
        
        boolean bottomLeftIsSafe = isAreaSafe(x, y);
        boolean bottomRightIsSafe = isAreaSafe(x + playerWidth - epsilon, y);
        boolean topLeftIsSafe = isAreaSafe(x, y + playerHeight - epsilon);
        boolean topRightIsSafe = isAreaSafe(x + playerWidth - epsilon, y + playerHeight - epsilon);

        // Safe only if ALL corners are on a road tile
        return bottomLeftIsSafe && bottomRightIsSafe && topLeftIsSafe && topRightIsSafe;
    }
    
    public boolean isAreaSafe(float x, float y) { // checks whether the tile at the given position is safe to move onto.
        int col = (int) floor( x);
        int row = (int) floor(y);

        // ensures the player is not moving outside the map boundaries
        if (roadLayer == null || col < 0 || row < 0 || col >= roadLayer.getWidth() || row >= roadLayer.getHeight()) {
            return false;
        }

        TiledMapTileLayer.Cell cell = roadLayer.getCell(col, row);
        // the getcell() will returns null if there is no tile on the roadlayer at this position
        return cell != null && cell.getTile() != null;
    }

    public void inputHandler(){
        float delta = Gdx.graphics.getDeltaTime();
        float speed = player.getSpeed();

        Vector2 currentPosition = player.getPlayerPosition();
        float newPositionX = currentPosition.x;
        float newPositionY = currentPosition.y;

        // Calculate the New movements based on input
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            newPositionX += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            newPositionX -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            newPositionY += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            newPositionY -= speed * delta;
        }

        // collisions check by Separating axes
        // check x-axes
        if (isTileSafe(newPositionX, currentPosition.y)) {
            player.move(newPositionX, currentPosition.y);
        } else {
            // stops movement outside that range by resetting position
            newPositionX = currentPosition.x;
        }

        // check y-axes
        if (isTileSafe(currentPosition.x, newPositionY)) {
            player.move(currentPosition.x, newPositionY);
        }

        // object handling
        for (int i = 0; i<interactables.getCount(); i++){
            MapObject Object = interactables.get(i);
            MapProperties ObjectProperties = Object.getProperties();
            
            Vector2 ObjectPosition = new Vector2((Float) ObjectProperties.get("x") * unitScale, (Float)ObjectProperties.get("y") * unitScale);
            Vector2 ObjectSize = new Vector2((float)ObjectProperties.get("width") * unitScale, (float) ObjectProperties.get("height") * unitScale);

            if (currentPosition.x >= (ObjectPosition.x) && currentPosition.x <= ((ObjectPosition.x + ObjectSize.x)) && // Checks if the player is inside the object area
                currentPosition.y >= (ObjectPosition.y) && currentPosition.y <= ((ObjectPosition.y + ObjectSize.y))){

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
