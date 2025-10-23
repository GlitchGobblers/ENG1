package io.github.some_example_name;

//class to store information about the player so that MapManager can draw the player


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;// used to store the location of the player
    private float speed = 3;// used to set the movement speed of the player
    private Texture texture;
    private SpriteBatch batch;
    public Player(Vector2 position) {
        this.texture = new Texture("Art/Characters/Main Character/Test Character.png");
        this.position = position;
        position.y = Gdx.graphics.getHeight() - (float) Gdx.graphics.getHeight() / 2;
        position.x = Gdx.graphics.getWidth() - (float) Gdx.graphics.getWidth() / 2;
        batch = new SpriteBatch();
    }

    public void create() {

    }

    public Vector2 get_player_position() {
        return position;
    }
    public void render() {
        batch.begin();
        batch.draw(this.texture, position.x, position.y);
        batch.end();
        Gdx.gl.glClearColor(0, 0, 0, 1);
    }
    public void dispose() {
        batch.dispose();
    }
    public void move(int x, int y) {// class to move the player inputs can be -1, 0 or 1
        position.x += x*speed;
        position.y += y*speed;
    }
}
