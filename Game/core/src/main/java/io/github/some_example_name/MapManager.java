package io.github.some_example_name;

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


//Main game class, will manage the camera and will store information about the map

public class MapManager implements Screen {

    private Game game;
    private String mapFilePath;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private OrthographicCamera camera;
    private FitViewport gameViewport;
    private OrthographicCamera timerCamera;
    private TiledMapTileLayer roadLayer;
    private TiledMapTileLayer winLayer;
    private SpriteBatch batch;
    private MapObjects interactables;
    private EventManager EM;
    private Timer timer;
    private final BitmapFont font;
    private GlyphLayout layout;
    private Texture interact;
    private int score = 0;
    private Music runningSound;

    // Pause state/UI
    private boolean paused = false;
    private boolean gameOver = false;
    private Stage uiStage;
    private Skin uiSkin;
    private Table pauseTable;
    private Table endTable;
    private Table passTable;

    private Texture barrierTexture; //the barrier texture
    private Rectangle barrierRect;
    private boolean isBarrierActive = true;
    private Texture keyTexture; // the key barrier texture
    private Rectangle keyRect;
    private boolean iskeyActive = true;
    private int eventCount = 0; //variable to keep counts how many times the key is collected
    private float unitScale = 1/16f;
    private SplashScreen.Difficulty difficulty = SplashScreen.Difficulty.EASY; // Default difficulty

    // Temporary code so that it will show whichever tilemap is in the file location, will have to move to render once things are moving
    public MapManager(Game game, String mapFile) {
        this(game, mapFile, SplashScreen.Difficulty.EASY);
    }

    public MapManager(Game game, String mapFile, SplashScreen.Difficulty difficulty) {
        this.difficulty = difficulty;
        this.game = game;
        this.mapFilePath = mapFile;

        // this file is a temporary one to see if the renderer is working, it's not our final one
        TiledMap map = new TmxMapLoader().load(mapFile);

        // out of all layers this is safe layer which the player can move on it.
        this.roadLayer = (TiledMapTileLayer) map.getLayers().get("Road");

        // This layer is the layer at which the game is won and ends
        this.winLayer = (TiledMapTileLayer) map.getLayers().get("WinCondition");

        // Gets all the interactables on the object layer
        interactables = map.getLayers().get("Interactables").getObjects();

        // Creates an EventManager which handles getting information about the events on the map
        EM = new EventManager(interactables);

        this.renderer = new OrthogonalTiledMapRenderer(map, unitScale);

        this.camera = new OrthographicCamera();

        this.gameViewport = new FitViewport(60, 40, camera);
        // temporary size to test, once developed slightly the user may be able to select the size of the game window in the main menu
        camera.setToOrtho(false, 60, 40);
        this.renderer.setView(camera);
        this.batch = new SpriteBatch();
        player = new Player(new Vector2(9.75f, 2));
        interact =  new Texture("Art/Interact.png");
        barrierTexture = new Texture("Art/Props/Crate_Medium_Closed.png");
        keyTexture = new Texture("Art/Characters/Main Character/Test Character2.png");
        barrierRect = new Rectangle(37, 21, 2, 2); // place the barrier on the map.
        keyRect = new Rectangle(3, 30, 2, 2); // place the key for the barrier on the map

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 48;
        this.font = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!

        runningSound = Gdx.audio.newMusic(Gdx.files.internal("Sound/running_sound.mp3"));
        runningSound.setLooping(true);
        runningSound.setVolume(0.45f);
    }

    @Override
    public void show() {
        timer = new Timer(difficulty.getMinutes());
        timer.startTimer();

        layout = new GlyphLayout();

        timerCamera = new OrthographicCamera();
        timerCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // UI for pause menu
        uiStage = new Stage(new ScreenViewport());
        uiSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        buildPauseUI();
        buildFailGUI();
        buildPassUI();
    }

    private void buildPauseUI() {
        pauseTable = new Table(uiSkin);
        pauseTable.setFillParent(true);
        pauseTable.defaults().pad(10);
        uiStage.addActor(pauseTable);

        Label title = new Label("Pause", uiSkin);
        title.setFontScale(3.6f);
        title.setAlignment(Align.center);
        TextButton resumeBtn = new TextButton("Resume", uiSkin);
        resumeBtn.getLabel().setFontScale(3.0f); // 3x larger
        TextButton restartBtn = new TextButton("Restart", uiSkin);
        restartBtn.getLabel().setFontScale(3.0f); // 3x larger
        TextButton quitBtn = new TextButton("Quit", uiSkin);
        quitBtn.getLabel().setFontScale(3.0f); // 3x larger

        Table window = new Table(uiSkin);
        window.defaults().pad(20).minWidth(200).minHeight(60); // Larger padding and min sizes for buttons
        window.add(title).center().padBottom(40).row();
        window.add(resumeBtn).fillX().minHeight(80).row();
        window.add(restartBtn).fillX().minHeight(80).row();
        window.add(quitBtn).fillX().minHeight(80);

        pauseTable.add().expand().row();
        pauseTable.add(window).center();
        pauseTable.row();
        pauseTable.add().expand();

        pauseTable.setVisible(false);

        resumeBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                togglePause();
            }
        });

        restartBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new SplashScreen(game));
            }
        });

        quitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit(); // Properly exit the application
            }
        });
    }

    private void buildFailGUI() {
        endTable = new Table(uiSkin);
        endTable.setFillParent(true);
        endTable.defaults().pad(10);
        uiStage.addActor(endTable);

        Label passTitle = new Label("You ran out of time and failed to escape university!", uiSkin);
        passTitle.setFontScale(3.6f);
        passTitle.setAlignment(Align.center);
        passTitle.setColor(new Color(0.7f, 0f, 0f, 1f));

        TextButton restartBtn = new TextButton("Restart", uiSkin);
        restartBtn.getLabel().setFontScale(3.0f);
        TextButton quitBtn = new TextButton("Quit", uiSkin);
        quitBtn.getLabel().setFontScale(3.0f);

        Table window = new Table(uiSkin);
        window.defaults().pad(20).minWidth(200).minHeight(60);
        window.add(passTitle).center().padBottom(40).row();
        window.add(restartBtn).fillX().minHeight(80).row();
        window.add(quitBtn).fillX().minHeight(80);

        endTable.add().expand().row();
        endTable.add(window).center();
        endTable.row();
        endTable.add().expand();

        endTable.setVisible(false);

        restartBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MapManager(game, mapFilePath, difficulty));
            }
        });

        quitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void buildPassUI() {
        passTable = new Table(uiSkin);
        passTable.setFillParent(true);
        passTable.defaults().pad(10);
        uiStage.addActor(passTable);

        Label passTitle = new Label("You solved the puzzles and ESCAPED THE UNI!", uiSkin);
        passTitle.setFontScale(3.6f);
        passTitle.setAlignment(Align.center);
        passTitle.setColor(new Color(1f, 0.84f, 0f, 1f));

        TextButton restartBtn = new TextButton("Play Again", uiSkin);
        restartBtn.getLabel().setFontScale(3.0f);
        TextButton quitBtn = new TextButton("Quit", uiSkin);
        quitBtn.getLabel().setFontScale(3.0f);

        Table window = new Table(uiSkin);
        window.defaults().pad(20).minWidth(200).minHeight(60);
        window.add(passTitle).center().padBottom(40).row();
        window.add(restartBtn).fillX().minHeight(80).row();
        window.add(quitBtn).fillX().minHeight(80);

        passTable.add().expand().row();
        passTable.add(window).center();
        passTable.row();
        passTable.add().expand();

        passTable.setVisible(false);

        restartBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(new MapManager(game, mapFilePath, difficulty));
            }
        });

        quitBtn.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    private void togglePause() {
        paused = !paused;
        if (paused) {
            if (timer != null) timer.pauseTimer();
            if (pauseTable != null) pauseTable.setVisible(true);
            if (runningSound != null && runningSound.isPlaying()) runningSound.pause();
            Gdx.input.setInputProcessor(uiStage);
        } else {
            if (timer != null) timer.startTimer();
            if (pauseTable != null) pauseTable.setVisible(false);
            Gdx.input.setInputProcessor(null);
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
        batch.setProjectionMatrix(camera.combined);// this ensures that player sprite use the same world units as the map.
        batch.begin();
        player.draw(batch);
        if (isBarrierActive && barrierRect != null && barrierTexture != null) { //draw the barrier only when the active states is true and it's not null
            batch.draw(barrierTexture, barrierRect.x, barrierRect.y, barrierRect.width, barrierRect.height);
        }
        if (iskeyActive && keyTexture != null) {// draw the key that will help the player to pass the barrier
            batch.draw(keyTexture, keyRect.x, keyRect.y, keyRect.width, keyRect.height);
        }
        batch.end();

        batch.setProjectionMatrix(timerCamera.combined); // player can see timer
        batch.begin();
        String timerText = timer.displayTimer();
        layout.setText(font, timerText);
        // appears in top right corner
        float x = Gdx.graphics.getWidth() - layout.width - 20;
        float y = Gdx.graphics.getHeight() - 20;
        font.setColor(Color.BLACK);
        font.draw(batch, timerText, x, y);

        // Display score in top left corner
        String scoreText = "Score: " + score;
        layout.setText(font, scoreText);
        float scoreX = 20;
        float scoreY = Gdx.graphics.getHeight() - 20;
        font.setColor(new Color(0.4f, 0.0f, 0.4f, 1.0f)); // Dark purple
        font.draw(batch, scoreText, scoreX, scoreY);
        batch.end();

        // Trigger game over when timer hits zero
        if (!gameOver && timer.getSeconds() <= 0) {
            gameOver = true;
            if (pauseTable != null) pauseTable.setVisible(false);
            if (endTable != null) endTable.setVisible(true);
            if (timer != null && timer.getRunning()) timer.pauseTimer();
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
            if (playerRect.overlaps(barrierRect)) return false;
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
                    String change = EM.event(i, player);
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

    /**
     * Modifies the score by the specified amount.
     * Can be positive (to increase) or negative (to decrease) the score.
     *
     * @param amount The amount to add to the score (can be positive or negative)
     */
    public void modifyScore(int amount) {
        score += amount;
    }
}
