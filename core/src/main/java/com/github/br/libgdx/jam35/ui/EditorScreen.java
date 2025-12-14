package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.ScreenLoader;
import com.github.br.libgdx.jam35.model.*;

public class EditorScreen implements Screen, GameModel.Listener {

    private static final int PADDING_UP = -10;

    private GameContext context;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;

    private GameType type;
    private final ScreenLoader screenLoader;

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

    public EditorScreen(GameContext context, ScreenLoader screenLoader) {
        this.context = context;
        this.screenLoader = screenLoader;
        this.gameFieldUi = new GameFieldUi(context);
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        this.type = GameType.EDITOR;

        runtimeFsm.reset();
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = context.getAssetManager().get(Res.SKIN);

        changeMode(this.type);
        showEditor();

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
        resetGameModel(gameModel);

        update(gameModel);
        gameModel.addListener(this);

        TextButton modeButton = createButton("RUNTIME", 750, 570);
        modeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                type = (GameType.EDITOR == type) ? GameType.QUIZ : GameType.EDITOR;
                String buttonText = (GameType.EDITOR == type) ? "RUNTIME" : "EDITOR";
                modeButton.setText(buttonText);
                changeMode(type);
            }
        });
        stage.addActor(modeButton);

        TextButton saveButton = createButton("SAVE", 750, 420);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameModel().saveGrid("levels/level_2.json");
            }
        });
        stage.addActor(saveButton);

        TextButton loadButton = createButton("LOAD", 750, 270);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameModel().loadGrid("levels/level_2.json");
            }
        });
        stage.addActor(loadButton);

        TextButton backToMenuButton = createButton("BACK", 750, 70);
        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                screenLoader.loadMainMenu();
            }
        });
        stage.addActor(backToMenuButton);

        Gdx.input.setInputProcessor(stage);
    }

    private static void resetGameModel(GameModel gameModel) {
        gameModel.reset();
        gameModel.initEmptyGrid();
        gameModel.addPlayer(PlayerColorType.WHITE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BLACK, UserType.COMPUTER);
        gameModel.setCurrentPlayer(0);
        gameModel.setNew(true);
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

        gameFieldUi.updateGrid();
        if (model.isGameEnd()) {
            // переход к следующему уровню по менюшке
            Player winner = model.getWinnerPlayer();
            System.out.println("game end. winner: " + winner);

            Window window = new Window("Player" + winner.getPlayerType().name() + "WIN!", skin);
            window.defaults().pad(4f);
            //window.add("").row();
            final TextButton button = new TextButton("OK", skin);
            button.pad(8f);
            button.addListener(new ChangeListener() {
                @Override
                public void changed(final ChangeEvent event, final Actor actor) {
                    resetGameModel(context.getGameModel());
                    window.remove();
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
        gameFieldUi.updateGridPosition(-140, stage);
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
