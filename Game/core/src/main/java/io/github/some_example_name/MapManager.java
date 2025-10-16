package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

//Main game class, will manage the camera and will store information about the map

public class MapManager implements Screen {

    private Game game;
    private OrthogonalTiledMapRenderer renderer;
    public MapManager(Game game){
        this.game = game;
        TiledMap map = new TmxMapLoader().load("maps/Tiled/Tilemaps/Beginning Fields.tmx");
        float unitScale = 1/16f;
        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);
        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(false,30,20);
        renderer.setView(camera);
        renderer.render();

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float v) {

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
