package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;


/** Used to load and start the game, as well as the background music. */
public class Main extends Game {
    private Music backgroundMusic;

    @Override
    public void create() {
        try {
            // create background music with mp3 from assets
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/background_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.05f); // Volume: 5%
            backgroundMusic.play();

            // opens the game to the splash screen
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

        // also disposes of the background music
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
    }
}