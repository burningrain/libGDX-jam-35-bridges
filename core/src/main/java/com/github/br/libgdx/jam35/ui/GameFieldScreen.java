package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Grid;

public class GameFieldScreen implements Screen, GameModel.Listener {

    private GameContext context;

    private Stage stage;
    private Skin skin;

    private final GameFieldUi gameFieldUi;
    private final UiFsm fsm;

    private final ClickListener cellListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            fsm.handle(currentCell);
        }
    };

    public GameFieldScreen(GameContext context) {
        this.context = context;
        this.gameFieldUi = new GameFieldUi();
        this.fsm = new UiFsm(gameFieldUi, context);
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = new Skin(Gdx.files.internal(Res.SKIN));
        update(context.getGameModel());
        context.getGameModel().addListener(this);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(GameModel model) {
        Grid modelGrid = model.getGrid();
        if (Grid.NULL_OBJECT == modelGrid) {
            return;
        }

        if (gameFieldUi.isEmpty()) {
            gameFieldUi.initGrid(createGrid(modelGrid));
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
        image.addListener(cellListener);

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
