package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

//Main game class, will manage the camera and will store information about the map

public class MapManager implements Screen {

    private Game game;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    // Temporary code so that it will show whichever tilemap is in the file location, will have to move to render once things are moving
    public MapManager(Game game){
        this.game = game;
        TiledMap map = new TmxMapLoader().load("maps/Tilesets/Starting Map.tmx");// this file is a temporary one to see if the renderer is working, its not our final one
        float unitScale = 1/16f;
        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false,30,20); // temporary size to test, once developed slightly the user may be able to select the size of the game window in the main menu
        this.renderer.setView(camera);
        this.renderer.render();
        player = new Player(new Vector2(15, 15));

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {
        this.renderer.render();
        player.render();
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
}
