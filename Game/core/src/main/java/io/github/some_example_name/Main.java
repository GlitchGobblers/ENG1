package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

// Use to load and start the game music and ui assets

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    SpriteBatch batch;
    FitViewport viewport;
    Sprite player;
    Texture background;

    @Override
    public void create() {
        setScreen(new MapManager(this));

        // both temporary until we have a proper background and player sprite
        player = new Sprite(new Texture("Art/Characters/Main Character/Character_Idle.png"));
        player.setSize(1, 1);
        background = new Texture("Art/Buildings/House_Hay_3.png");

        batch = new SpriteBatch();
        viewport = new FitViewport(8, 5);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // organise code into three methods
        input();
        logic();
        draw();
    }

    private void input() {
        // libgdx wants floats for everything
        float speed = 4f;
        // delta time to avoid speed discrepancies with different framerates
        float delta = Gdx.graphics.getDeltaTime();

        // separated if statements so we can move in multiple directions at once
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.translateX(speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.translateX(-speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            player.translateY(speed * delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            player.translateY(-speed * delta);
        }
    }

    private void logic() {

    }

    private void draw() {
        // clears the screen - should be done every frame, or we get "graphical errors"
        ScreenUtils.clear(Color.BLACK);

        // boilerplate
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(background, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight()); // draw the background
        player.draw(batch);

        batch.end();
    }
}
