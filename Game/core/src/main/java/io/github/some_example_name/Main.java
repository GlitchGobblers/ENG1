package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

// Use to load and start the game music and ui assets

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    SpriteBatch batch;
    FitViewport viewport;
    Texture playerSprite;

    @Override
    public void create() {
        setScreen(new MapManager(this));

        playerSprite = new Texture("Art/Characters/Main Character/Character_Idle.png");

        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {

    }

    private void logic() {

    }

    private void draw() {
        // clears the screen - should be done every frame, or we get graphical errors
        ScreenUtils.clear(Color.BLACK);
        // boilerplate
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(playerSprite, 0, 0, 1, 1);

        batch.end();
    }
}
