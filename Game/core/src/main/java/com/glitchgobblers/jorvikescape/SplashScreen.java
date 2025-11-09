package com.glitchgobblers.jorvikescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The class for the splashscreen which shows at the beginning of a session
 */
public class SplashScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;

    /**
     * An Enum containing data about the various difficulties and timer changes as a result
     */
    public enum Difficulty {
        EASY(5 * 60),
        MEDIUM((int) (4.5 * 60)),
        HARD(4 * 60);

        private final int timeInSeconds;

        Difficulty(int time) {
            this.timeInSeconds = time;
        }

        public int getTime() {
            return timeInSeconds;
        }
    }

    private Difficulty currentDifficulty;
    private TextButton difficultyButton;
    private Button playButton;

    // Font for title
    private BitmapFont titleFont;
    private GlyphLayout titleLayout;

    // For drawing play triangle
    private ShapeRenderer shapeRenderer;

    public SplashScreen(Game game) {
        this.game = game;
        this.currentDifficulty = Difficulty.EASY;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture("Art/Splash_Screen.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Create title font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/arial.ttf"));

        // modify font parameters to allow for correct styling
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        parameter.borderWidth = 4;
        parameter.borderColor = Color.BLACK;

        titleFont = generator.generateFont(parameter);
        generator.dispose();
        titleFont.setColor(Color.WHITE);

        titleLayout = new GlyphLayout();

        // Setup UI stage
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Gdx.input.setInputProcessor(stage);

        shapeRenderer = new ShapeRenderer();

        buildUI();
    }

    private void buildUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Title text will be drawn separately, so add spacing for it
        mainTable.add().expandY().row();

        // How to Play button
        TextButton.TextButtonStyle howToPlayStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        howToPlayStyle.fontColor = Color.WHITE;

        // Set the dark green colour with 75% opacity
        Color greenColor = new Color(0.1f, 0.3f, 0.1f, 0.75f);
        Color greenBorderColor = new Color(greenColor.r * 0.75f, greenColor.g * 0.75f, greenColor.b * 0.75f, 0.75f);

        Color greenPressed = new Color(greenColor.r * 0.7f, greenColor.g * 0.7f, greenColor.b * 0.7f, 0.75f);
        Color greenPressedBorder = new Color(greenPressed.r * 0.75f, greenPressed.g * 0.75f, greenPressed.b * 0.75f, 0.75f);

        Color greenOver = new Color(greenColor.r * 0.9f, greenColor.g * 0.9f, greenColor.b * 0.9f, 0.75f);
        Color greenOverBorder = new Color(greenOver.r * 0.75f, greenOver.g * 0.75f, greenOver.b * 0.75f, 0.75f);

        howToPlayStyle.up = createColoredDrawableWithBorder(greenColor, greenBorderColor, 400, 70, 3);
        howToPlayStyle.down = createColoredDrawableWithBorder(greenPressed, greenPressedBorder, 400, 70, 3);
        howToPlayStyle.over = createColoredDrawableWithBorder(greenOver, greenOverBorder, 400, 70, 3);

        TextButton howToPlayButton = new TextButton("How To Play", howToPlayStyle);
        howToPlayButton.getLabel().setFontScale(1.8f);

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HowToPlayScreen(game));
            }
        });

        // Add the new how to play button above the difficulty selector
        mainTable.add(howToPlayButton).size(400, 70).padBottom(20).row();

        // Difficulty button - brown/map coloured, 75% opacity
        TextButton.TextButtonStyle difficultyStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        difficultyStyle.fontColor = Color.WHITE;

        // Set brown/map colour with 75% opacity
        Color brownColor = new Color(0.545f, 0.412f, 0.294f, 0.75f);
        Color brownBorderColor = new Color(brownColor.r * 0.75f, brownColor.g * 0.75f, brownColor.b * 0.75f, 0.75f); // Slightly darker border
        Color brownPressed = new Color(brownColor.r * 0.7f, brownColor.g * 0.7f, brownColor.b * 0.7f, 0.75f);
        Color brownPressedBorder = new Color(brownPressed.r * 0.75f, brownPressed.g * 0.75f, brownPressed.b * 0.75f, 0.75f);
        Color brownOver = new Color(brownColor.r * 0.9f, brownColor.g * 0.9f, brownColor.b * 0.9f, 0.75f);
        Color brownOverBorder = new Color(brownOver.r * 0.75f, brownOver.g * 0.75f, brownOver.b * 0.75f, 0.75f);

        difficultyStyle.up = createColoredDrawableWithBorder(brownColor, brownBorderColor, 400, 70, 3);
        difficultyStyle.down = createColoredDrawableWithBorder(brownPressed, brownPressedBorder, 400, 70, 3);
        difficultyStyle.over = createColoredDrawableWithBorder(brownOver, brownOverBorder, 400, 70, 3);

        difficultyButton = new TextButton("Select Difficulty: Easy", difficultyStyle);
        difficultyButton.getLabel().setFontScale(1.8f);

        difficultyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                cycleDifficulty();
            }
        });

        // Increased padding to move the play button lower
        mainTable.add(difficultyButton).size(400, 70).padBottom(20).row();

        playButton = createPlayButton();
        mainTable.add(playButton).padBottom(60).row();

        mainTable.add().expandY();
    }

    /**
     * Creates a play button - stone grey, 75% opacity, with green play triangle
     *
     * @return a {@link Button} instance configured as the play button
     */
    private Button createPlayButton() {
        Button.ButtonStyle playButtonStyle = new Button.ButtonStyle();
        Color stoneGrey = new Color(0.5f, 0.5f, 0.5f, 0.5f); // Stone grey with 50% opacity
        Color stoneGreyBorder = new Color(stoneGrey.r * 0.75f, stoneGrey.g * 0.75f, stoneGrey.b * 0.75f, 0.5f); // Slightly darker border
        Color pressedGrey = new Color(stoneGrey.r * 0.7f, stoneGrey.g * 0.7f, stoneGrey.b * 0.7f, 0.5f);
        Color pressedGreyBorder = new Color(pressedGrey.r * 0.75f, pressedGrey.g * 0.75f, pressedGrey.b * 0.75f, 0.5f);
        Color overGrey = new Color(stoneGrey.r * 0.9f, stoneGrey.g * 0.9f, stoneGrey.b * 0.9f, 0.5f);
        Color overGreyBorder = new Color(overGrey.r * 0.75f, overGrey.g * 0.75f, overGrey.b * 0.75f, 0.5f);

        playButtonStyle.up = createColoredDrawableWithBorder(stoneGrey, stoneGreyBorder, 100, 100, 3);
        playButtonStyle.down = createColoredDrawableWithBorder(pressedGrey, pressedGreyBorder, 100, 100, 3);
        playButtonStyle.over = createColoredDrawableWithBorder(overGrey, overGreyBorder, 100, 100, 3);

        Button button = new Button(playButtonStyle);
        button.setSize(100, 100);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });

        return button;
    }

    private Drawable createColoredDrawableWithBorder(Color fillColor, Color borderColor, int width, int height, int borderWidth) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        // Draw border
        pixmap.setColor(borderColor);
        pixmap.fill();

        // Draw fill (inside area, leaving border)
        pixmap.setColor(fillColor);
        pixmap.fillRectangle(borderWidth, borderWidth, width - borderWidth * 2, height - borderWidth * 2);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();

        return new TextureRegionDrawable(new com.badlogic.gdx.graphics.g2d.TextureRegion(texture));
    }

    /**
     * Cycles through the difficulty levels in a predefined order (EASY -> MEDIUM -> HARD -> EASY).
     */
    private void cycleDifficulty() {
        switch (currentDifficulty) {
            case EASY:
                currentDifficulty = Difficulty.MEDIUM;
                difficultyButton.setText("Select Difficulty: Medium");
                break;
            case MEDIUM:
                currentDifficulty = Difficulty.HARD;
                difficultyButton.setText("Select Difficulty: Hard");
                break;
            case HARD:
                currentDifficulty = Difficulty.EASY;
                difficultyButton.setText("Select Difficulty: Easy");
                break;
        }
    }

    private void startGame() {
        game.setScreen(new MapManager(game, "maps/Tilesets/Starting Map.tmx", currentDifficulty));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // draws in the splash screen texture
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Draw title text with a border (title font already has border from FreeType)
        String titleText = "Escape Jorvik University!";
        titleLayout.setText(titleFont, titleText);

        // positions it in the centre and near the top
        float titleX = (Gdx.graphics.getWidth() - titleLayout.width) / 2;
        float titleY = Gdx.graphics.getHeight() - 150;
        titleFont.draw(batch, titleText, titleX, titleY);

        batch.end();

        // Draw play triangle on play button
        stage.act(delta);

        // Get play button position and draw green triangle
        if (playButton != null && playButton.isVisible()) {
            // Get button position in stage coordinates
            float buttonX = playButton.getX() + playButton.getWidth() / 2;
            float buttonY = playButton.getY() + playButton.getHeight() / 2;

            // Slightly smaller to match the button's size
            float triangleSize = 35;

            shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0.0f, 0.95f, 0.0f, 1.0f); // Green

            // Draw triangle pointing right (play symbol) - centered
            float offsetX = triangleSize * 0.02f; // Minimal offset for better centering
            shapeRenderer.triangle(
                buttonX - triangleSize / 3 + offsetX, buttonY - triangleSize / 2,
                buttonX - triangleSize / 3 + offsetX, buttonY + triangleSize / 2,
                buttonX + triangleSize / 2 + offsetX, buttonY
            );
            shapeRenderer.end();
        }

        // Draw UI
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
        if (camera != null) {
            camera.setToOrtho(false, width, height);
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
        // Clear the input processor when leaving this screen
        Gdx.input.setInputProcessor(null);
    }

    /**
     * Disposes of everything that needs to be disposed of, so we avoid memory leaks.
     */
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (titleFont != null) titleFont.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}