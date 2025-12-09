package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.CellType;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Grid;

public class GameFieldScreen implements Screen, GameModel.Listener {

    private GameContext context;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm runtimeFsm;

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
            Cell model = currentCell.getModel();
            CellType currentType = model.getType();
            switch (currentType) {
                case EMPTY:
                    currentType = CellType.OUR_CELL;
                    break;
                case OUR_CELL:
                    currentType = CellType.ENEMY_CELL;
                    break;
                case ENEMY_CELL:
                    currentType = CellType.EMPTY;
                    break;
            }

            model.setType(currentType);
            currentCell.setModelCellType(currentType);
        }
    };

    public GameFieldScreen(GameContext context, GameType type) {
        this.context = context;
        this.gameFieldUi = new GameFieldUi();
        this.runtimeFsm = new UiFsm(gameFieldUi, context);
        changeMode(type);
    }

    public void changeMode(GameType type) {
        runtimeFsm.reset();

        ClickListener currentListener = (GameType.EDITOR == type) ? editorListener : cellListener;
        gameFieldUi.changeListener(currentListener);
        this.type = type;
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = new Skin(Gdx.files.internal(Res.SKIN));
        update(context.getGameModel());
        context.getGameModel().addListener(this);

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
                context.getGameModel().saveGrid("levels/level_1.json");
            }
        });
        stage.addActor(saveButton);

        TextButton loadButton = createButton("LOAD", 950, 670);
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                context.getGameModel().loadGrid("levels/level_1.json");
            }
        });
        stage.addActor(loadButton);
        //TODO убрать в отедльный скрин позже

        Gdx.input.setInputProcessor(stage);
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

        if (model.isNew()) {
            model.setNew(false);
            gameFieldUi.initGrid(createGrid(modelGrid));
            changeMode(type);
        }

    }

    private CellImage[][] createGrid(Grid modelGrid) {
        Cell[][] grid = modelGrid.getGrid();

        int padding = 32;
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        float cellSize = 64 + padding;
        float leftX = (width - cellSize * grid.length + padding) / 2f;
        float leftY = (height - cellSize * grid[0].length + padding) / 2f;

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

        stage.getViewport().update(width, height);
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
