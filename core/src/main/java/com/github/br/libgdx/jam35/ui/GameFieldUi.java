package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Player;
import com.github.br.libgdx.jam35.model.WasJump;

public class GameFieldUi {

    private CellImage[][] cells;

    public boolean isEmpty() {
        return cells == null;
    }

    public void initGrid(CellImage[][] cells) {
        this.cells = cells;
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
        Cell currentModel = currentCell.getModel();
        return playerWhoDoStep == currentModel.getPlayer();
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

}
