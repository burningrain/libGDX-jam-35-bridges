package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.*;

public class GameFieldUi {

    private static final int CELL_PADDING_RIGHT = 20;
    private static final float CELL_SIZE = 64f;

    private int paddingUp;

    private final GameContext context;

    private CellImage[][] cells;

    public GameFieldUi(GameContext context) {
        this.context = context;
    }

    public boolean isEmpty() {
        return cells == null;
    }

    public void initGrid(int paddingUp, Stage stage, Grid modelGrid) {
        this.paddingUp = paddingUp;
        this.cells = createGrid(paddingUp, stage, modelGrid);
    }

    public void updateGrid() {
        for (CellImage[] row : cells) {
            for (CellImage cellImage : row) {
                Cell model = cellImage.getModel();
                cellImage.setPlayerColor(model.getPlayer());
            }
        }
    }

    public boolean isOurCell(CellImage currentCell, Player playerWhoDoStep) {
        if (currentCell == null) {
            throw new IllegalArgumentException("currentCell is null");
        }
        if (playerWhoDoStep == null) {
            throw new IllegalArgumentException("playerWhoDoStep is null");
        }

        Cell currentModel = currentCell.getModel();
        return playerWhoDoStep.equals(currentModel.getPlayer());
    }

    public void selectFutureCells(GameContext context, CellImage currentCell, Array<CellImage> selectedFutureStepCells) {
        GameModel gameModel = context.getGameModel();
        Cell cellModel = currentCell.getModel();

        Array<Cell> stepsForCell = gameModel.getPossibleStepsForCell(cellModel, new WasJump());
        for (Cell cell : stepsForCell) {
            CellImage cellImage = cells[cell.getX()][cell.getY()];
            cellImage.setSelectType(CellImageType.FUTURE_STEP);
            selectedFutureStepCells.add(cellImage);
        }
    }

    public void deselectFutureCells(Array<CellImage> futureStepCells) {
        for (CellImage futureStepCell : futureStepCells) {
            futureStepCell.setSelectType(CellImageType.NONE);
        }
    }

    public void select(CellImage currentCell) {
        currentCell.setSelectType(CellImageType.SELECTED);
    }

    public void deselect(CellImage cell) {
        if (cell == null) {
            return;
        }
        cell.setSelectType(CellImageType.NONE);
    }

    public void changeListener(ClickListener currentListener) {
        if (cells == null) {
            return;
        }

        for (CellImage[] column : cells) {
            for (CellImage cellImage : column) {
                cellImage.clearListeners();
                cellImage.addListener(currentListener);
            }
        }
    }

    public void updateGridPosition(Stage stage) {
        if (cells == null || cells.length == 0) {
            return;
        }

        float viewportWidth = stage.getWidth();
        float viewportHeight = stage.getHeight();

        int gridWidth = cells.length;
        int gridHeight = cells[0].length;

        float cellSizeWithPadding = CELL_SIZE + CELL_PADDING_RIGHT;
        float leftX = (viewportWidth - cellSizeWithPadding * gridWidth + CELL_PADDING_RIGHT) / 2f;
        float leftY = paddingUp + (viewportHeight - cellSizeWithPadding * gridHeight + CELL_PADDING_RIGHT) / 2f;
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                CellImage image = cells[i][j];
                if (image != null) {
                    image.setPosition(leftX + i * cellSizeWithPadding,
                        leftY + j * cellSizeWithPadding);
                }
            }
        }
    }

    private CellImage[][] createGrid(int paddingUp, Stage stage, Grid modelGrid) {
        Cell[][] grid = modelGrid.getGrid();

        float width = stage.getWidth();
        float height = stage.getHeight();

        float cellSize = CELL_SIZE + CELL_PADDING_RIGHT;
        float leftX = (width - cellSize * grid.length + CELL_PADDING_RIGHT) / 2f;
        float leftY = paddingUp + (height - cellSize * grid[0].length + CELL_PADDING_RIGHT) / 2f;

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

}
