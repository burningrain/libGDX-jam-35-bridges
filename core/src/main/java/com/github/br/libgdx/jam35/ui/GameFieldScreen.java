package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.*;

public class GameFieldScreen implements Screen, GameModel.Listener {

    private static final int PADDING_UP = -12;
    public static final String LEVEL_TEXT = "LEVEL: ";

    private GameContext context;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;

    private GameType type;
    private Label levelLabel;

    private byte levelNumber = 0;

    private final ClickListener cellListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            runtimeFsm.handle(currentCell);
        }
    };
    private final ClickListener editorListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            Cell cellModel = currentCell.getModel();

            GameModel gameModel = context.getGameModel();
            int playersCount = gameModel.getPlayersCount();
            int playerId;
            Player player = cellModel.getPlayer();
            if (player == Player.NULL_PLAYER) {
                playerId = -1;
            } else {
                playerId = player.getId();
            }

            if (playerId == (playersCount - 1)) {
                playerId = -1;
            } else {
                playerId++;
            }

            cellModel.setPlayer((playerId == -1) ? Player.NULL_PLAYER : gameModel.getPlayer(playerId));
            currentCell.setPlayerColor(cellModel.getPlayer());
        }
    };

    public GameFieldScreen(GameContext context, GameType type) {
        this.context = context;
        this.gameFieldUi = new GameFieldUi();
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        this.type = type;

        runtimeFsm.reset();
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = new Skin(Gdx.files.internal(Res.SKIN));

        changeMode(this.type);
        if (GameType.EDITOR == this.type) {
            showEditor();
        } else if (GameType.RUNTIME == this.type) {
            showRuntime();
        }

        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();
        resize(currentWidth, currentHeight);
    }

    public void changeMode(GameType type) {
        runtimeFsm.reset();

        ClickListener currentListener = (GameType.EDITOR == type) ? editorListener : cellListener;
        gameFieldUi.changeListener(currentListener);
        this.type = type;
    }

    private void showEditor() {
        GameModel gameModel = context.getGameModel();
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.addPlayer(PlayerColorType.WHITE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BLACK, UserType.COMPUTER);
        gameModel.setCurrentPlayer(0);

        update(gameModel);
        gameModel.addListener(this);

        //TODO убрать в отдельный скрин позже
        TextButton modeButton = createButton("RUNTIME", 950, 730);
        modeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                type = (GameType.EDITOR == type) ? GameType.RUNTIME : GameType.EDITOR;
                String buttonText = (GameType.EDITOR == type) ? "RUNTIME" : "EDITOR";
                modeButton.setText(buttonText);
                changeMode(type);
            }
        });
        stage.addActor(modeButton);

        TextButton saveButton = createButton("SAVE", 950, 700);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameModel().saveGrid("levels/level_2.json");
            }
        });
        stage.addActor(saveButton);

        TextButton loadButton = createButton("LOAD", 950, 670);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameModel().loadGrid("levels/level_2.json");
            }
        });
        stage.addActor(loadButton);

        Gdx.input.setInputProcessor(stage);
    }

    private void showRuntime() {
        levelLabel = createLevelLabel();
        stage.addActor(levelLabel);

        GameModel gameModel = context.getGameModel();
        update(gameModel);
        gameModel.addListener(this);

        startLevel(levelNumber, gameModel);
        Gdx.input.setInputProcessor(stage);
    }

    private void startLevel(byte levelNumber, GameModel gameModel) {
        levelLabel.setText(LEVEL_TEXT + (levelNumber + 1));

        gameModel.reset();
        gameModel.loadGrid("levels/level_" + levelNumber + ".json");
        gameModel.addPlayer(PlayerColorType.WHITE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BLACK, UserType.COMPUTER);
        gameModel.setCurrentPlayer(0);
        gameModel.start();
    }

    private Label createLevelLabel() {
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float leftX = width / 2f - 22f;
        float leftY = height + PADDING_UP * 3.2f;
        Label levelLabel = new Label(LEVEL_TEXT, skin);
        levelLabel.setX(leftX);
        levelLabel.setY(leftY);

        return levelLabel;
    }

    private TextButton createButton(String title, int x, int y) {
        TextButton modeButton = new TextButton(title, skin);
        modeButton.setX(x);
        modeButton.setY(y);
        return modeButton;
    }

    @Override
    public void update(GameModel model) {
        Grid modelGrid = model.getGrid();
        if (Grid.NULL_OBJECT == modelGrid) {
            return;
        }

        // инициализация нового уровня / новой игры
        if (model.isNew()) {
            model.setNew(false);
            gameFieldUi.initGrid(createGrid(modelGrid));
            changeMode(type);
            return;
        }

        gameFieldUi.updateGrid();
        if (model.isGameEnd()) {
            // переход к следующему уровню по менюшке
            Player winner = model.getWinnerPlayer();
            System.out.println("game end. winner: " + winner);

            levelNumber++;

            Window window = new Window("YOU WIN!", skin, "border");
            window.defaults().pad(4f);
            //window.add("").row();
            final TextButton button = new TextButton("Next", skin);
            button.pad(8f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    window.remove();
                    startLevel(levelNumber, context.getGameModel());
                }
            });
            window.add(button);
            window.pack();
            // We round the window position to avoid awkward half-pixel artifacts.
            // Casting using (int) would also work.
            window.setPosition(MathUtils.roundPositive(stage.getWidth() / 2f - window.getWidth() / 2f),
                MathUtils.roundPositive(stage.getHeight() / 2f - window.getHeight() / 2f));
            window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));
            stage.addActor(window);
        }

    }

    private CellImage[][] createGrid(Grid modelGrid) {
        Cell[][] grid = modelGrid.getGrid();

        int paddingRight = 20;
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        float cellSize = 64 + paddingRight;
        float leftX = (width - cellSize * grid.length + paddingRight) / 2f;
        float leftY = PADDING_UP + (height - cellSize * grid[0].length + paddingRight) / 2f;

        CellImage[][] result = new CellImage[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                CellImage image = createCell(grid[i][j], leftX + i * cellSize, leftY + j * cellSize);
                stage.addActor(image);
                result[i][j] = image;
            }
        }
        return result;
    }

    private CellImage createCell(Cell cell, float x, float y) {
        AssetManager assetManager = context.getAssetManager();

        CellImage image = new CellImage(
            cell,
            new TextureRegion(assetManager.get(Res.CELL, Texture.class)),
            new TextureRegion(assetManager.get(Res.SELECTED_CELL, Texture.class)),
            new TextureRegion(assetManager.get(Res.FUTURE_CELL, Texture.class))
        );
        image.setPosition(x, y);

        return image;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
    }

}
