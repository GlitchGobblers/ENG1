package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.audio.Music;


import static java.lang.Math.floor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import com.badlogic.gdx.math.Rectangle;


/**
 * Main game class; manages the camera and stores information about the map
 */
public class MapManager implements Screen {
    private final Game game;
    private final OrthogonalTiledMapRenderer renderer;
    private final Player player;
    private final OrthographicCamera camera;
    private final FitViewport gameViewport;
    private OrthographicCamera timerCamera;
    private final TiledMapTileLayer roadLayer;
    private final TiledMapTileLayer winLayer;
    private final SpriteBatch batch;
    private final MapObjects interactables;
    private final EventManager EM;
    private Timer timer;
    private final BitmapFont font;
    private GlyphLayout layout;
    private final Texture interact;
    private final Music runningSound;

    // Pause state/UI
    private boolean paused = false;
    private boolean gameOver = false;

    private Stage uiStage;
    private Skin uiSkin;

    private Table pauseTable;
    private Table endTable;
    private Table passTable;

    private final Texture barrierTexture;
    private final Rectangle barrierRect;
    private boolean isBarrierActive = true;

    private final Texture keyTexture;
    private final Rectangle keyRect;
    private boolean iskeyActive = true;

    // keeps count of how many times the key has been collected
    private int eventCount = 0;

    private final SplashScreen.Difficulty difficulty;

    public MapManager(Game game, String mapFile, SplashScreen.Difficulty difficulty) {
        this.difficulty = difficulty;
        this.game = game;

        // this file is temporary to see if the renderer is working, it's not our final one
        TiledMap map = new TmxMapLoader().load(mapFile);

        // this is a safe layer which the player can move on
        this.roadLayer = (TiledMapTileLayer) map.getLayers().get("Road");

        // This layer is the layer at which the game is won and ends
        this.winLayer = (TiledMapTileLayer) map.getLayers().get("WinCondition");

        // Gets all the interactables on the object layer
        interactables = map.getLayers().get("Interactables").getObjects();

        // Creates an EventManager which handles getting information about the events on the map
        EM = new EventManager(interactables);

        float unitScale = 1 / 16f;
        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        this.camera = new OrthographicCamera();

        this.gameViewport = new FitViewport(60, 40, camera);
        camera.setToOrtho(false, 60, 40);
        this.renderer.setView(camera);

        this.batch = new SpriteBatch();
        player = new Player(new Vector2(9.75f, 3));
        interact = new Texture("Art/Interact.png");

        // place the barrier on the map
        barrierRect = new Rectangle(37, 21, 2, 2);
        barrierTexture = new Texture("Art/Props/Crate_Medium_Closed.png");

        // place the key for the barrier on the map
        keyRect = new Rectangle(10, 23, 2, 2);
        keyTexture = new Texture("Art/Characters/Main Character/Test Character2.png");

        // sets up font for text rendering of timer/score
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        this.font = generator.generateFont(parameter);
        generator.dispose();

        runningSound = Gdx.audio.newMusic(Gdx.files.internal("Sound/running_sound.mp3"));
        runningSound.setLooping(true);
        runningSound.setVolume(0.45f);
    }

    @Override
    public void show() {
        layout = new GlyphLayout();

        timer = new Timer(difficulty.getTime());
        timer.startTimer();

        timerCamera = new OrthographicCamera();
        timerCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // UI for pause menu
        uiStage = new Stage(new ScreenViewport());
        uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        pauseTable = buildUI("Pause", Color.BLACK);
        endTable = buildUI("You ran out of time and failed to escape university!", new Color(0.7f, 0f, 0f, 1f));
        passTable = buildUI("You solved the puzzles and ESCAPED THE UNI!", new Color(1f, 0.84f, 0f, 1f));
    }

    private Table buildUI(String mainTitle, Color titleColour) {
        Table table = new Table(uiSkin);
        table.setFillParent(true);
        table.defaults().pad(10);
        uiStage.addActor(table);

        Label title = new Label(mainTitle, uiSkin);
        title.setFontScale(3.6f);
        title.setAlignment(Align.center);
        title.setColor(titleColour);

        Table window = new Table(uiSkin);
        window.defaults().pad(20).minWidth(200).minHeight(60); // Larger padding and min sizes for buttons
        window.add(title).center().padBottom(40).row();

        // only for the pause screen do we have a resume button
        if (mainTitle.equals("Pause")) {
            TextButton resumeBtn = new TextButton("Resume", uiSkin);
            resumeBtn.getLabel().setFontScale(3.0f); // 3x larger

            window.add(resumeBtn).fillX().minHeight(80).row();

            resumeBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    togglePause();
                }
            });
        }

        TextButton restartBtn = new TextButton("Restart", uiSkin);
        restartBtn.getLabel().setFontScale(3.0f); // 3x larger
        restartBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SplashScreen(game));
            }
        });

        window.add(restartBtn).fillX().minHeight(80).row();

        TextButton quitBtn = new TextButton("Quit", uiSkin);
        quitBtn.getLabel().setFontScale(3.0f); // 3x larger
        quitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit(); // Properly exit the application
            }
        });

        window.add(quitBtn).fillX().minHeight(80);

        table.add().expand().row();
        table.add(window).center();
        table.row();
        table.add().expand();

        table.setVisible(false);

        return table;
    }

    private void togglePause() {
        // switches the state of whether it is currently paused
        paused = !paused;

        if (paused) {
            // assuming the timer exists, pause it
            if (timer != null) {
                timer.pauseTimer();
            }

            // pauses the running sound, if it exists and is running
            if (runningSound != null && runningSound.isPlaying()) {
                runningSound.pause();
            }

            Gdx.input.setInputProcessor(uiStage);
        } else {
            // assuming the timer exists, start it
            if (timer != null) {
                timer.resumeTimer();
            }

            Gdx.input.setInputProcessor(null);
        }

        // assuming the pause table exists, show it if we are paused
        if (pauseTable != null) {
            pauseTable.setVisible(paused);
        }
    }

    @Override
    public void render(float v) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Apply the viewport (important for proper rendering with black bars)
        gameViewport.apply();
        
        this.renderer.setView(camera);
        this.renderer.render();

        // Toggle pause on ESC (disabled during game over)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) && !gameOver) {
            togglePause();
        }

        // Only process gameplay when not paused or game over
        if (!paused && !gameOver) {
            inputHandler();
            player.update(Gdx.graphics.getDeltaTime());
        }

        // this ensures that player sprite uses the same world units as the map
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        player.draw(batch);

        // draw the barrier only when it's active and not null
        if (isBarrierActive && (barrierRect != null) && (barrierTexture != null)) {
            batch.draw(barrierTexture, barrierRect.x, barrierRect.y, barrierRect.width, barrierRect.height);
        }

        // draw the key that will help the player to pass the barrier
        if (iskeyActive && keyTexture != null) {
            batch.draw(keyTexture, keyRect.x, keyRect.y, keyRect.width, keyRect.height);
        }

        batch.end();

        // player can see the timer
        batch.setProjectionMatrix(timerCamera.combined);

        batch.begin();
        String timerText = timer.displayTimer();
        layout.setText(font, timerText);

        // appears in the top right corner
        float x = Gdx.graphics.getWidth() - layout.width - 20;
        float y = Gdx.graphics.getHeight() - 20;
        font.setColor(Color.BLACK);
        font.draw(batch, timerText, x, y);
        
        // Display score in the top left corner
        int score = 0;
        String scoreText = "Score: " + score;
        layout.setText(font, scoreText);

        float scoreX = 20;
        float scoreY = Gdx.graphics.getHeight() - 20;

        font.setColor(new Color(0.4f, 0.0f, 0.4f, 1.0f)); // Dark purple
        font.draw(batch, scoreText, scoreX, scoreY);
        batch.end();

        // Trigger game over when timer hits zero
        if (!gameOver && timer.getTimeLeft() <= 0) {
            gameOver = true;

            if (timer.getRunning()) {
                timer.pauseTimer();
            }

            if (pauseTable != null) {
                pauseTable.setVisible(false);
            }

            if (endTable != null) {
                endTable.setVisible(true);
            }

            Gdx.input.setInputProcessor(uiStage);
        }

        // Draw UI overlays
        if (paused || gameOver) {
            uiStage.act();
            uiStage.draw();
        }
    }

    @Override
    public void resize(int i, int i1) {
        // Update the game viewport to maintain aspect ratio
        if (gameViewport != null) {
            gameViewport.update(i, i1, true);
        }
        if (uiStage != null) {
            uiStage.getViewport().update(i, i1, true);
        }
        if (timerCamera != null) {
            timerCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
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
        if (uiStage != null) uiStage.dispose();
        if (uiSkin != null) uiSkin.dispose();
        if (runningSound != null) runningSound.dispose();
    }

    public boolean isTileSafe(float x, float y) {
        float playerWidth = player.getWidth();
        float playerHeight = player.getHeight();
        float epsilon = 0.01f; // Prevents snagging on exact tile edges


        boolean bottomLeft = IsAreaSafe(x, y);
        boolean bottomRight = IsAreaSafe(x + playerWidth - epsilon, y);
        boolean topLeft = IsAreaSafe(x, y + playerHeight - epsilon);
        boolean topRight = IsAreaSafe(x + playerWidth - epsilon, y + playerHeight - epsilon);
        //Safe only if ALL corners are on a road tile


        if (!(bottomLeft && bottomRight && topLeft && topRight)) return false;

        // player must not overlaps the barrier if it's active
        if (isBarrierActive && barrierRect != null) {
            Rectangle playerRect = new Rectangle(x, y, playerWidth, playerHeight);
            return !playerRect.overlaps(barrierRect);
        }

        return true;
    }

    private boolean playerOnWinTile(float x, float y) {
        float w = player.getWidth();
        float h = player.getHeight();
        float eps = 0.01f;

        boolean bl = isVictoryArea(x, y);
        boolean br = isVictoryArea(x + w - eps, y);
        boolean tl = isVictoryArea(x, y + h - eps);
        boolean tr = isVictoryArea(x + w - eps, y + h - eps);

        return bl || br || tl || tr;
    }

    public boolean IsAreaSafe(float x, float y) { // checks whether the tile at the given position is safe to move onto.
        int col = (int) floor(x);
        int row = (int) floor(y);

        if (roadLayer == null || col < 0 || row < 0 || col >= roadLayer.getWidth() || row >= roadLayer.getHeight()) {
            return false;
        } // the statement ensures the player is not moving outside the map boundaries.

        TiledMapTileLayer.Cell cell = roadLayer.getCell(col, row);
        return cell != null && cell.getTile() != null;
        //the getcell() will returns null if there is no tile on the roadlayer at this position
    }

    public boolean isVictoryArea(float x, float y) {
        int col = (int) floor(x);
        int row = (int) floor(y);

        if (winLayer == null || col < 0 || row < 0 || col >= winLayer.getWidth() || row >= winLayer.getHeight()) {
            return false;
        }

        TiledMapTileLayer.Cell cell = winLayer.getCell(col, row);
        return cell != null && cell.getTile() != null;
    }

    public void objectCheck(Vector2 CurrentPosition) {
        for (int i = 0; i < interactables.getCount(); i++) {
            MapObject Object = interactables.get(i);
            MapProperties ObjectProperties = Object.getProperties();
            Vector2 ObjectPosition = new Vector2((Float) ObjectProperties.get("x") / 16, (Float) ObjectProperties.get("y") / 16);
            Vector2 ObjectSize = new Vector2((float) ObjectProperties.get("width") / 16, (float) ObjectProperties.get("height") / 16);
            if (CurrentPosition.x >= (ObjectPosition.x) && CurrentPosition.x <= ((ObjectPosition.x + ObjectSize.x)) && // Checks if the player is inside the object area
                CurrentPosition.y >= (ObjectPosition.y) && CurrentPosition.y <= ((ObjectPosition.y + ObjectSize.y))) {
                batch.begin();
                batch.draw(interact, CurrentPosition.x*20, (CurrentPosition.y+2 )*20);
                batch.end();
                if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                    String change = EM.event(i);
                    if (Float.parseFloat(change) <= 10) {
                        player.setSpeed((float) Integer.parseInt(change));
                    }
                }
            }
        }
    }

    public void inputHandler() {
        float delta = Gdx.graphics.getDeltaTime();
        float speed = player.getSpeed();

        Vector2 CurrentPosition = player.getPlayerPosition();
        float NewPositionX = CurrentPosition.x;
        float NewPositionY = CurrentPosition.y;

        // Calculate the New movements based on input
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        if (right) { NewPositionX += speed * delta; }
        if (left) { NewPositionX -= speed * delta; }
        if (up) { NewPositionY += speed * delta; }
        if (down) { NewPositionY -= speed * delta; }

        Vector2 p = player.getPlayerPosition();
        if (playerOnWinTile(p.x, p.y)) {
            gameOver = true;
            if (pauseTable != null) pauseTable.setVisible(false);
            if (endTable != null) endTable.setVisible(false);   // hide fail table if it exists
            if (passTable != null) passTable.setVisible(true);  // SHOW the pass table
            if (timer != null && timer.getRunning()) timer.pauseTimer();
            Gdx.input.setInputProcessor(uiStage);
        }

        // collisions check by Separating axes
        // check x-axes
        if (isTileSafe(NewPositionX, CurrentPosition.y)) {
            player.move(NewPositionX, CurrentPosition.y);
        } else {
            NewPositionX = CurrentPosition.x; // use current x if move failed
        }


        //check y-axes
        if (isTileSafe(CurrentPosition.x, NewPositionY)) {
            player.move(CurrentPosition.x, NewPositionY);
        } else {
            NewPositionX = CurrentPosition.x; // use current x if move failed
        }

        // Update player animation state based on input
        boolean moving = right || left || up || down;
        // Running sound effect control
        if (moving && !runningSound.isPlaying()) {
            runningSound.play();
        } else if (!moving && runningSound.isPlaying()) {
            runningSound.pause();
        }
        Player.Direction dir = null;
        if (right) dir = Player.Direction.RIGHT;
        else if (left) dir = Player.Direction.LEFT;
        else if (up) dir = Player.Direction.UP;
        else if (down) dir = Player.Direction.DOWN;
        player.setAnimationState(moving, dir);
        Rectangle playerRect = new Rectangle(CurrentPosition.x, CurrentPosition.y, player.getWidth(), player.getHeight());

        if (iskeyActive && playerRect.overlaps(keyRect)) { //this will if statment will check if the player touches the key if yes it'll remove the barrier
            iskeyActive = false;
            isBarrierActive = false;
            eventCount++;
            System.out.println("the key is been collected"+ eventCount);
        }

        objectCheck(CurrentPosition);
    }
}
