package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    private Stage stage;

    public GameFieldUi(GameContext context) {
        this.context = context;
    }

    public boolean isEmpty() {
        return cells == null;
    }

    public void initGrid(int paddingUp, Stage stage, Grid modelGrid) {
        this.paddingUp = paddingUp;
        this.stage = stage;
        this.cells = (cells == null) ? createGrid(paddingUp, stage, modelGrid) : updateGridByModel(modelGrid);
    }

    public CellImage[][] updateGridByModel(Grid modelGrid) {
        Cell[][] grid = modelGrid.getGrid();
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                CellImage cellImage = cells[cell.getX()][cell.getY()];
                cellImage.setSelectType(CellImageType.NONE);
                cellImage.setModel(cell);
                cellImage.setPlayerColor(cell.getPlayer());
            }
        }

        return this.cells;
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

    public void updateGridPosition(int paddingX, Stage stage) {
        if (cells == null || cells.length == 0) {
            return;
        }

        float viewportWidth = stage.getWidth();
        float viewportHeight = stage.getHeight();

        int gridWidth = cells.length;
        int gridHeight = cells[0].length;

        float cellSizeWithPadding = CELL_SIZE + CELL_PADDING_RIGHT;
        float leftX = paddingX + (viewportWidth - cellSizeWithPadding * gridWidth + CELL_PADDING_RIGHT) / 2f;
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
        TextureAtlas atlas = assetManager.get(Res.CLOUDS_AND_BRIDGES);

        CellImage image = new CellImage(
            cell,
            atlas.findRegion(Res.Cloud.EMPTY),
            atlas.findRegion(Res.Cloud.YELLOW),
            atlas.findRegion(Res.Cloud.VIOLET),
            atlas.findRegion(Res.Cloud.BLUE),
            atlas.findRegion(Res.Cloud.BROWN)
        );
        image.setPosition(x, y);

        return image;
    }

    public Bridge createBridge(Cell modelFrom, Cell modelTo, PlayerColorType playerType, boolean jump) {
        CellImage from = cells[modelFrom.getX()][modelFrom.getY()];
        CellImage to = cells[modelTo.getX()][modelTo.getY()];

        AssetManager assetManager = context.getAssetManager();
        TextureAtlas atlas = assetManager.get(Res.CLOUDS_AND_BRIDGES);
        Array<TextureAtlas.AtlasRegion> regions = atlas.findRegions(getRegionName(playerType));
        Animation animation = new Animation((1 / 20f), regions);
        animation.setPlayMode(Animation.PlayMode.NORMAL);

        Bridge bridge = new Bridge(animation);
        bridge.setX(getBridgeX(from, to));
        bridge.setY(getBridgeY(from, to));

        bridge.setRotation(getRotation(from.getModel(), to.getModel()));

        bridge.setScaleX(Math.max(1, Math.abs(to.getModel().getX() - from.getModel().getX())));
        bridge.setScaleY(Math.max(1, Math.abs(to.getModel().getY() - from.getModel().getY())));


        return bridge;
    }

    // Вспомогательный метод для получения X центра CellImage
    private float getCellCenterX(CellImage cellImage) {
        return cellImage.getX() + cellImage.getWidth() / 2f;
    }

    // Вспомогательный метод для получения Y центра CellImage
    private float getCellCenterY(CellImage cellImage) {
        return cellImage.getY() + cellImage.getHeight() / 2f;
    }

    // Переписываем основные методы размещения моста
    private float getBridgeX(CellImage fromImage, CellImage toImage) {
        float centerX1 = getCellCenterX(fromImage);
        float centerX2 = getCellCenterX(toImage);
        // Координата X середины между центрами ячеек
        return (centerX1 + centerX2) / 2f;
    }

    private float getBridgeY(CellImage fromImage, CellImage toImage) {
        float centerY1 = getCellCenterY(fromImage);
        float centerY2 = getCellCenterY(toImage);
        // Координата Y середины между центрами ячеек
        return (centerY1 + centerY2) / 2f;
    }

    private float getRotation(Cell from, Cell to) {
        if (to.getY() > from.getY()) {
            return 0f;
        } else if (to.getY() < from.getY()) {
            return 180f;
        } else if (to.getX() > from.getX()) {
            return 270f;
        } else if (to.getX() < from.getX()) {
            return -180f;
        }

        return 0f;
    }

    private String getRegionName(PlayerColorType playerType) {
        switch (playerType) {
            case YELLOW:
                return "yellow_small_bridge";
            case VIOLET:
                return "volet_small_bridge";
            case BLUE:
                return "blue_small_bridge";
            case BROWN:
                return "brown_small_bridge";
        }
        throw new IllegalArgumentException("unknown playerType: " + playerType);
    }

    public void changeCellType(Cell midCell, Player nullPlayer) {
        CellImage cellImage = cells[midCell.getX()][midCell.getY()];
        cellImage.setPlayerColor(nullPlayer);
    }

    public void addToField(Bridge bridge) {
        stage.addActor(bridge);
    }

}
