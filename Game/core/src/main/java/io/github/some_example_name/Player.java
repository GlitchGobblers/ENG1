package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;// used to store the location of the player
    private float speed = 3f;// used to set the movement speed of the player
    private Texture texture;
    private Sprite playerSprite;
    //private SpriteBatch batch;
    private float width;
    private float height;
    // texture size from pixel to world units(1 unit=16 pixel)
    float unitScale=1/16f;

    public Player(Vector2 position) {
        this.texture = new Texture("Art/Characters/Main Character/Test Character.png");
        // using sprite gives more control of the texture
        this.playerSprite = new Sprite(texture);

        this.position = position;

        this.width = (float) texture.getWidth() * unitScale;
        this.height = (float) texture.getHeight() * unitScale;

        this.playerSprite.setSize(width, height);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getPlayerPosition() {
        return position;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed){
        this.speed = speed;
    }

    public void draw(SpriteBatch batch) {
        // set sprite position to match player's world coordinates
        playerSprite.setPosition(position.x, position.y);// position.x and position.y are in world units
        playerSprite.draw(batch);

    }

    public void move(float x, float y) {// set player new position
        position.x = x;
        position.y = y;
    }
}
