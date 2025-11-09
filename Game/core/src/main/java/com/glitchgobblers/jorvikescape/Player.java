package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Stores information about the player
 */
public class Player {
    public enum Direction { DOWN, LEFT, RIGHT, UP }

    private final Vector2 position;
    // used to set the player's movement speed
    private float speed = 6.2f;

    private final Animation<TextureRegion>[] idleAnimations; // indexed by Direction.ordinal()
    private final Animation<TextureRegion>[] walkAnimations; // indexed by Direction.ordinal()
    private float stateTime = 0f;
    private Direction currentDirection = Direction.DOWN;
    private boolean isMoving = false;

    // Collision size in world units (hitbox)
    private final float width;
    private final float height;

    // Visual size in world units (rendered sprite)
    private final float renderWidth;
    private final float renderHeight;

    public Player(Vector2 position, float unitScale) {
        this.position = position;

        // Load spritesheets
        // Animation/state
        Texture idleSheet = new Texture("Art/Characters/Main Character/Character_Idle.png");
        Texture walkSheet = new Texture("Art/Characters/Main Character/Character_Walk.png");

        // Use nearest filtering for crisp pixel art
        idleSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        walkSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        // Each sheet has 4 columns (frames) x 4 rows (directions)
        int idleFrameWidth = idleSheet.getWidth() / 4;
        int idleFrameHeight = idleSheet.getHeight() / 4;
        int walkFrameWidth = walkSheet.getWidth() / 4;
        int walkFrameHeight = walkSheet.getHeight() / 4;

        TextureRegion[][] idleRegions = TextureRegion.split(idleSheet, idleFrameWidth, idleFrameHeight);
        TextureRegion[][] walkRegions = TextureRegion.split(walkSheet, walkFrameWidth, walkFrameHeight);

        // Determine visual size based on frame
        this.renderWidth = idleFrameWidth * unitScale;
        this.renderHeight = idleFrameHeight * unitScale;

        // Use a 1x1 tile collision box to match map tiles
        this.width = 1f;
        this.height = 1f;

        // Map Direction enum ordinals to sprite sheet rows
        // Row order in spritesheet: row 0 = LEFT, row 1 = RIGHT, row 2 = UP, row 3 = DOWN
        // Direction enum order: DOWN (0), LEFT (1), RIGHT (2), UP (3)
        int[] rowForDir = {3, 0, 1, 2}; // Maps Direction ordinal to sprite sheet row

        // Build animations for 4 directions (each row has 4 frames that animate in sequence)
        idleAnimations = new Animation[4];
        walkAnimations = new Animation[4];

        for (int dir = 0; dir < 4; dir++) {
            int spriteRow = rowForDir[dir];
            // Each row contains 4 frames that will animate in sequence
            idleAnimations[dir] = new Animation<>(0.15f, idleRegions[spriteRow]);
            idleAnimations[dir].setPlayMode(Animation.PlayMode.LOOP);

            walkAnimations[dir] = new Animation<>(0.12f, walkRegions[spriteRow]);
            walkAnimations[dir].setPlayMode(Animation.PlayMode.LOOP);
        }
    }
    public void changeSpeed(float change){
        speed = speed * change;
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

    public void setAnimationState(boolean moving, Direction direction) {
        this.isMoving = moving;
        if (direction != null) {
            this.currentDirection = direction;
        }
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public void draw(SpriteBatch batch) {
        // Choose the current frame based on movement and facing
        Animation<TextureRegion> anim = isMoving ?
                walkAnimations[currentDirection.ordinal()] :
                idleAnimations[currentDirection.ordinal()];
        TextureRegion frame = anim.getKeyFrame(stateTime, true);

        // Draw using visual size; collision uses width/height (1x1)
        batch.draw(frame, position.x, position.y, renderWidth, renderHeight);
    }

    public void move(float x, float y) {
        position.x = x;
        position.y = y;
    }
}
