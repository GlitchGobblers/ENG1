package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.audio.Music;



// Use to load and start the game music and ui assets

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    private SpriteBatch batch;
    private Texture image;
    private Music backgroundMusic;

    @Override
    public void create() {
        try {

            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/background_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.2f); // Volume: 20%
            backgroundMusic.play();

            setScreen(new SplashScreen(this));
        } catch (Exception e) {
            // This will print the actual game-crashing error to the console
            e.printStackTrace();
            // Exit the application gracefully
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}