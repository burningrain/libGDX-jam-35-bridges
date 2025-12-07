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
import com.badlogic.gdx.utils.Array;
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

    private CellImage[][] cells;

    private CellImage from;
    private CellImage to;
    private final Array<CellImage> selectedFutureStepCells = new Array<>(4);

    private InterfaceFSM currentFsmState = InterfaceFSM.SELECT_CELL_FROM;

    public enum InterfaceFSM {
        SELECT_CELL_FROM,
        SELECT_CELL_TO,
        STEP
    }

    private final ClickListener cellListener = new ClickListener() {
        @Override
        public void clicked(InputEvent event, float x, float y) {
            CellImage currentCell = (CellImage) event.getTarget();
            CellImageType type = currentCell.getType();

            if (currentFsmState == InterfaceFSM.SELECT_CELL_FROM) {
                // валидация
                if (!isAbleToFromSelect(currentCell)) {
                    return;
                }
                if (type != CellImageType.NONE) {
                    return;
                }

                deselect(from);
                deselectFutureCells(selectedFutureStepCells);
                select(currentCell);
                selectFutureCells(currentCell, selectedFutureStepCells);
                from = currentCell;

                currentFsmState = InterfaceFSM.SELECT_CELL_TO;
            } else if (currentFsmState == InterfaceFSM.SELECT_CELL_TO) {
                switch (type) {
                    case SELECTED:
                        deselect(from);
                        deselectFutureCells(selectedFutureStepCells);
                        from = null;

                        currentFsmState = InterfaceFSM.SELECT_CELL_FROM;
                        break;
                    case FUTURE_STEP:
                        to = currentCell;
                        GameModel gameModel = context.getGameModel();

                        Cell modelFrom = from.getModel();
                        Cell modelTo = to.getModel();
                        from = null;
                        to = null;
                        selectedFutureStepCells.clear();

                        currentFsmState = InterfaceFSM.STEP;
                        gameModel.step(modelFrom, modelTo);
                        break;
                }
            }
        }
    };

    private boolean isAbleToFromSelect(CellImage currentCell) {
        Cell currentModel = currentCell.getModel();
        return CellType.OUR_CELL == currentModel.getType();
    }

    private void selectFutureCells(CellImage currentCell, Array<CellImage> selectedFutureStepCells) {
        GameModel gameModel = context.getGameModel();
        Cell cellModel = currentCell.getModel();
        int currentX = cellModel.getX();
        int currentY = cellModel.getY();

        // налево
        if (gameModel.isAbleToStep(currentX - 1, currentY)) {
            selectedFutureStepCells.add(cells[currentX - 1][currentY]);
        }

        // направо
        if (gameModel.isAbleToStep(currentX + 1, currentY)) {
            selectedFutureStepCells.add(cells[currentX + 1][currentY]);
        }

        // вверх
        if (gameModel.isAbleToStep(currentX, currentY + 1)) {
            selectedFutureStepCells.add(cells[currentX][currentY + 1]);
        }

        // вниз
        if (gameModel.isAbleToStep(currentX, currentY - 1)) {
            selectedFutureStepCells.add(cells[currentX][currentY - 1]);
        }

        for (CellImage selectedFutureStepCell : selectedFutureStepCells) {
            selectedFutureStepCell.setSelectType(CellImageType.FUTURE_STEP);
        }
    }

    private void deselectFutureCells(Array<CellImage> futureStepCells) {
        for (CellImage futureStepCell : futureStepCells) {
            futureStepCell.setSelectType(CellImageType.NONE);
        }
        futureStepCells.clear();
    }

    private void select(CellImage currentCell) {
        currentCell.setSelectType(CellImageType.SELECTED);
    }

    private void deselect(CellImage cell) {
        if (cell == null) {
            return;
        }
        cell.setSelectType(CellImageType.NONE);
    }

    public GameFieldScreen(GameContext context) {
        this.context = context;
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

        if (cells == null) {
            cells = createGrid(modelGrid);
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
