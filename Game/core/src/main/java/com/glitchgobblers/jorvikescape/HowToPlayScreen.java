package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

// A simple screen to display game instructions and setting summary
public class HowToPlayScreen implements Screen {

    private final Game game;
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    // Fonts for drawing text
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private GlyphLayout layout;

    // Text content
    private final String titleText = "How To Play";
    private final String settingText = "You have travelled back through time to old England where The University of York has been replaced by Jorvik University! The campus you once knew and loved is no longer. Instead, you must navigate the Viking-esque landscape between different University buildings, solving puzzles and occasionally coming across advantageous or even detrimental events. Time is running out. Will you find your way to the submission room before the deadline hits? Can you escape the university with your degree in hand?";
    private final String controlsText = "Use the W, A, S, D Keys to move your character around the map. Interact with objects by pressing the E key when you see the interaction prompt. Try to maximise your score while keeping an eye on the timer at the top right of the screen - don't let it run out!\nDifficulties: Easy: 5 minutes, Medium: 4.5 minutes, Hard: 4 minutes";


    public HowToPlayScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Prepare the screen here
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        batch = new SpriteBatch();
        layout = new GlyphLayout();

        // Use FreeType to generate nice fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ui/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter titleParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParameter.size = 56;
        titleParameter.color = Color.WHITE;
        titleParameter.borderColor = Color.BLACK;
        titleParameter.borderWidth = 2;
        titleFont = generator.generateFont(titleParameter);

        FreeTypeFontGenerator.FreeTypeFontParameter bodyParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bodyParameter.size = 24;
        bodyParameter.color = Color.WHITE;
        bodyFont = generator.generateFont(bodyParameter);
        generator.dispose();

        buildUI();

        Gdx.input.setInputProcessor(stage);
    }

    private void buildUI() {
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.bottom().padBottom(40); // Align to bottom with padding
        stage.addActor(mainTable);

        // Create a back button to return to the splash screen
        TextButton backButton = new TextButton("Back", skin);
        backButton.getLabel().setFontScale(2.0f);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SplashScreen(game));
            }
        });

        mainTable.add(backButton).size(250, 70);
    }

    @Override
    public void render(float delta) {
        // Draw the how to play screen here.
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1); // Dark grey background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw the title in the top center
        layout.setText(titleFont, titleText);
        float titleX = (Gdx.graphics.getWidth() - layout.width) / 2;
        float titleY = Gdx.graphics.getHeight() - 80;
        titleFont.draw(batch, layout, titleX, titleY);

        // Draw the setting summary below the title
        float textWidth = Gdx.graphics.getWidth() * 0.8f; // Text block is 80% of screen width
        layout.setText(bodyFont, settingText, Color.WHITE, textWidth, Align.center, true);
        float settingX = (Gdx.graphics.getWidth() - textWidth) / 2;
        float settingY = titleY - 100;
        bodyFont.draw(batch, layout, settingX, settingY);
        
        // Draw the controls text below the setting summary
        layout.setText(bodyFont, controlsText, Color.WHITE, textWidth, Align.center, true);
        float controlsY = settingY - layout.height - 50; // Add padding between sections
        bodyFont.draw(batch, layout, settingX, controlsY);

        batch.end();

        // Act and draw the UI stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized, width and height are 0 which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a normal size before updating.
        if (width <= 0 || height <= 0) return;
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Invoked when application is paused.
    }

    @Override
    public void resume() {
        // Invoked when application is resumed.
    }

    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {
        // Destroy screen's assets here.
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
        if (batch != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (bodyFont != null) bodyFont.dispose();
    }
}