package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

    private static final int PADDING_UP = -30;
    public static final String LEVEL_TEXT = "LEVEL: ";

    private final GameContext context;

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
        this.gameFieldUi = new GameFieldUi(context);
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        this.type = type;

        runtimeFsm.reset();
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = context.getAssetManager().get(Res.SKIN);
        runtimeFsm.setStage(stage);
        runtimeFsm.setSkin(skin);

        changeMode(this.type);
        showRuntime();

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

    private void showRuntime() {
        levelLabel = createLevelLabel();
        stage.addActor(levelLabel);

        GameModel gameModel = context.getGameModel();
        startLevel(levelNumber, gameModel);

        update(gameModel);
        gameModel.addListener(this);

        Gdx.input.setInputProcessor(stage);
    }

    private static void resetGameModel(GameModel gameModel) {
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.addPlayer(PlayerColorType.YELLOW, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.VIOLET, UserType.COMPUTER);
        gameModel.setCurrentPlayer(0);
        gameModel.setNew(true);
    }

    private void startLevel(byte levelNumber, GameModel gameModel) {
        levelLabel.setText(LEVEL_TEXT + (levelNumber + 1));
        resetGameModel(gameModel);
        gameModel.loadGrid("levels/level_" + levelNumber + ".json");
        gameModel.start();
    }

    private Label createLevelLabel() {
        float width = stage.getViewport().getWorldWidth();
        float height = stage.getViewport().getWorldHeight();
        float leftX = width / 2f - 120f;
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
            gameFieldUi.initGrid(PADDING_UP, stage, modelGrid);
            changeMode(type);
            return;
        }

        gameFieldUi.updateGridByModel(modelGrid);
        if (model.isGameEnd()) {
            // переход к следующему уровню по менюшке
            Player winner = model.getWinnerPlayer();
            System.out.println("game end. winner: " + winner);

            levelNumber++;

            UiUtils.createWindow(stage, skin, "YOU WIN!", "Next", new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    startLevel(levelNumber, context.getGameModel());
                }
            });
        }

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(51f / 255f, 204f / 255f, 255f / 255f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        stage.getViewport().update(width, height, true);
        gameFieldUi.updateGridPosition(0, stage);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        context.getGameModel().removeListener(this);
    }

    @Override
    public void dispose() {
        context.getGameModel().removeListener(this);

        stage.dispose();
        skin.dispose();
    }

}
