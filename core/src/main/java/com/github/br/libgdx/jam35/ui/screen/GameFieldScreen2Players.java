package com.github.br.libgdx.jam35.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.github.br.libgdx.jam35.model.exception.NeedToJumpException;
import com.github.br.libgdx.jam35.model.step.Step;
import com.github.br.libgdx.jam35.model.step.UiStepVisitor;
import com.github.br.libgdx.jam35.ui.utils.CellImage;
import com.github.br.libgdx.jam35.ui.utils.GameFieldUi;
import com.github.br.libgdx.jam35.ui.UiFsm;
import com.github.br.libgdx.jam35.ui.utils.UiUtils;

public class GameFieldScreen2Players implements Screen, GameModel.Listener {

    private static final int PADDING_UP = -10;
    private static final int PADDING_X = -140;

    private final GameContext context;
    private final ScreenLoader screenLoader;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;
    private final UiStepVisitor uiStepVisitor;

    private final ClickListener cellListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            try {
                runtimeFsm.handle(currentCell);
            } catch (GameException e) {
                UiUtils.createWindow(stage, skin, e.getMessage(), "OK", new ChangeListener() {
                    @Override
                    public void changed(final ChangeEvent event, final Actor actor) {
                    }
                });
            }
        }
    };

    public GameFieldScreen2Players(GameContext context, ScreenLoader screenLoader) {
        this.context = context;
        this.gameFieldUi = new GameFieldUi(context);
        uiStepVisitor = new UiStepVisitor(gameFieldUi);
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        this.screenLoader = screenLoader;
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = context.getAssetManager().get(Res.SKIN);

        runtimeFsm.reset();
        showRuntime();

        int currentWidth = Gdx.graphics.getWidth();
        int currentHeight = Gdx.graphics.getHeight();

        resize(currentWidth, currentHeight);
    }

    private void showRuntime() {
        GameModel gameModel = context.getGameModel();
        resetGameModel(gameModel);
        setLevelSettings(gameModel);
        gameModel.start();

        update(gameModel);
        gameFieldUi.changeListener(cellListener);
        gameModel.addListener(this);

        TextButton backToMenuButton = UiUtils.createButton(skin, "BACK", 750, 70);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenLoader.loadMainMenu();
            }
        });
        stage.addActor(backToMenuButton);

        Gdx.input.setInputProcessor(stage);
    }

    protected void setLevelSettings(GameModel gameModel) {
        gameModel.loadGrid("levels/2_players.json");
        gameModel.addPlayer(PlayerColorType.BLUE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BROWN, UserType.HUMAN);
        gameModel.setCurrentPlayer(1);
    }

    private void resetGameModel(GameModel gameModel) {
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.setNew(true);
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
