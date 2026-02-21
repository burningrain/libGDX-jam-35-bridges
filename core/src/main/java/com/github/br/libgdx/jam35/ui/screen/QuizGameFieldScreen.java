package com.github.br.libgdx.jam35.ui.screen;

import com.badlogic.gdx.InputProcessor;
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
import com.github.br.libgdx.jam35.model.exception.GameException;
import com.github.br.libgdx.jam35.model.exception.LevelIsNotSupportedException;
import com.github.br.libgdx.jam35.model.step.Step;
import com.github.br.libgdx.jam35.ui.UiStepVisitor;
import com.github.br.libgdx.jam35.ui.UiFsm;
import com.github.br.libgdx.jam35.ui.utils.CellImage;
import com.github.br.libgdx.jam35.ui.utils.GameFieldUi;
import com.github.br.libgdx.jam35.ui.utils.UiUtils;

public class QuizGameFieldScreen implements GameScreen, GameModel.Listener {

    private static final int PADDING_UP = -30;
    private static final int PADDING_X = -140;
    private static final String LEVEL_TEXT = "LEVEL: ";

    private final GameContext context;
    private final ScreenLoader screenLoader;

    private final Stage stage;
    private final Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;
    private final UiStepVisitor uiStepVisitor;

    private final Label levelLabel;

    private byte currentLevelNumber = 0;

    private final UiUtils uiUtils;
    private final ClickListener cellListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            try {
                runtimeFsm.handle(currentCell);
            } catch (GameException e) {
                uiUtils.createWindow(stage, skin, e.getMessage(), "OK", new ChangeListener() {
                    @Override
                    public void changed(final ChangeEvent event, final Actor actor) {
                    }
                });
            }
        }
    };

    public QuizGameFieldScreen(UiUtils uiUtils, GameContext context, ScreenLoader screenLoader) {
        this.uiUtils = uiUtils;
        this.context = context;
        this.screenLoader = screenLoader;

        this.skin = context.getAssetManager().get(Res.SKIN);
        this.stage = new Stage(context.getViewport());
        this.levelLabel = createLevelLabel();
        stage.addActor(levelLabel);

        this.gameFieldUi = new GameFieldUi(uiUtils, context);
        this.uiStepVisitor = new UiStepVisitor(gameFieldUi);
        this.runtimeFsm = new UiFsm(gameFieldUi, context);

        TextButton restartButton = uiUtils.createButton(skin, "RESTART", 750, 270);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameModel gameModel = context.getGameModel();
                restart(gameModel);
            }
        });
        stage.addActor(restartButton);

        TextButton backToMenuButton = uiUtils.createButton(skin, "BACK", 750, 70);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenLoader.loadMainMenu();
            }
        });
        stage.addActor(backToMenuButton);
    }

    @Override
    public void show() {
        runtimeFsm.reset();
        GameModel gameModel = context.getGameModel();
        gameModel.addListener(this);

        restart(gameModel);
    }

    private void restart(GameModel gameModel) {
        startLevel(currentLevelNumber, gameModel);
        update(gameModel);
        gameFieldUi.changeListener(cellListener);
    }

    private static void resetGameModel(GameModel gameModel, int levelNumber) {
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.addPlayer(PlayerColorType.YELLOW, UserType.HUMAN);
        gameModel.addPlayer(getComputerPlayerByLevel(levelNumber), UserType.COMPUTER);
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
                return PlayerColorType.BLUE;
            default:
                throw new LevelIsNotSupportedException(levelNumber);
        }
    }

    private void startLevel(byte levelNumber, GameModel gameModel) {
        levelLabel.setText(LEVEL_TEXT + (levelNumber + 1));
        resetGameModel(gameModel, levelNumber);
        gameModel.loadGrid("levels/level_" + levelNumber + ".json");
        gameModel.start(getGameMode());
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

    @Override
    public void update(GameModel model) {
        Grid modelGrid = model.getGrid();
        if (Grid.NULL_OBJECT == modelGrid) {
            return;
        }

        // инициализация нового уровня / новой игры
        if (model.isNew()) {
            model.setNew(false);
            gameFieldUi.initGrid(PADDING_X, PADDING_UP, stage, modelGrid);
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
                    currentLevelNumber++;

                    if (currentLevelNumber == 8) { //TODO брать число уровней
                        uiUtils.createWindow(stage, skin, "THANK YOU FOR PLAYING!", "Menu", new ChangeListener() {
                            @Override
                            public void changed(final ChangeEvent event, final Actor actor) {
                                currentLevelNumber = 0;
                                screenLoader.loadMainMenu();
                            }
                        });
                    } else {
                        uiUtils.createWindow(stage, skin, "YOU WIN!", "Next", new ChangeListener() {
                            @Override
                            public void changed(final ChangeEvent event, final Actor actor) {
                                restart(context.getGameModel());
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

    @Override
    public InputProcessor getStage() {
        return stage;
    }

    public GameModeType getGameMode() {
        return GameModeType.TWO_PLAYERS;
    }

}
