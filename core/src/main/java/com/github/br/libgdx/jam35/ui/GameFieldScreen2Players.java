package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.ScreenLoader;
import com.github.br.libgdx.jam35.model.*;
import com.github.br.libgdx.jam35.model.step.Step;
import com.github.br.libgdx.jam35.model.step.UiStepVisitor;

public class GameFieldScreen2Players implements Screen, GameModel.Listener {

    private static final int PADDING_UP = -30;
    public static final String LEVEL_TEXT = "LEVEL: ";

    private final GameContext context;
    private final ScreenLoader screenLoader;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;
    private UiStepVisitor uiStepVisitor;

    private GameType type;

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

    private boolean isFourPlayers;

    public GameFieldScreen2Players(GameContext context, GameType type, ScreenLoader screenLoader, boolean isFourPlayers) {
        this.context = context;
        this.gameFieldUi = new GameFieldUi(context);
        uiStepVisitor = new UiStepVisitor(gameFieldUi);
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        this.type = type;
        this.screenLoader = screenLoader;
        this.isFourPlayers = isFourPlayers;

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

        GameModel gameModel = context.getGameModel();
        startLevel(gameModel);

        update(gameModel);
        gameModel.addListener(this);

        Gdx.input.setInputProcessor(stage);
    }

    private void resetGameModel(GameModel gameModel) {
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.addPlayer(PlayerColorType.BLUE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BROWN, UserType.HUMAN);
        if (this.isFourPlayers) {
            gameModel.addPlayer(PlayerColorType.YELLOW, UserType.HUMAN);
            gameModel.addPlayer(PlayerColorType.VIOLET, UserType.HUMAN);
        }

        gameModel.setCurrentPlayer(0);
        gameModel.setNew(true);
    }

    private static PlayerColorType getComputerPlayerByLevel(int levelNumber) {
        switch (levelNumber) {
            case 0:
            case 1:
            case 2:
                return PlayerColorType.VIOLET;
            case 3:
            case 4:
            case 5:
                return PlayerColorType.BROWN;
            case 6:
            case 7:
            case 8:
                return PlayerColorType.BLUE;
            default:
                throw new IllegalArgumentException("levelNumber is not supported: " + levelNumber);
        }
    }

    private void startLevel(GameModel gameModel) {
        resetGameModel(gameModel);
        if (isFourPlayers) {
            gameModel.loadGrid("levels/level_4_players.json");
        } else {
            gameModel.loadGrid("levels/level_2_players.json");
        }

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


        Array<Step> steps = model.pollCurrentSteps();
        for (Step step : steps) {
            step.visit(uiStepVisitor);
        }

        uiStepVisitor.executeWhenStepQueueIsEnd(new Runnable() {
            @Override
            public void run() {
                gameFieldUi.updateGridByModel(modelGrid);
                if (model.isGameEnd()) {
                    // переход к следующему уровню по менюшке
                    Player winner = model.getWinnerPlayer();

                    if (winner != null) {
                        UiUtils.createWindow(stage, skin,
                            "Player '" + winner.getPlayerColorType() + "' WIN!", "Menu",
                            new ChangeListener() {
                                @Override
                                public void changed(final ChangeEvent event, final Actor actor) {
                                    screenLoader.loadMainMenu();
                                }
                            });
                    }
                }
            }
        });

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(51f / 255f, 204f / 255f, 255f / 255f, 1f);
        uiStepVisitor.act(delta);
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
