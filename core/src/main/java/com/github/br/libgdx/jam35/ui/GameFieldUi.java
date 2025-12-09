package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.CellType;
import com.github.br.libgdx.jam35.model.GameModel;

public class GameFieldUi {

    private CellImage[][] cells;

    public boolean isEmpty() {
        return cells == null;
    }

    public void initGrid(CellImage[][] cells) {
        this.cells = cells;
    }

    public boolean isOurCell(CellImage currentCell) {
        Cell currentModel = currentCell.getModel();
        return CellType.OUR_CELL == currentModel.getType();
    }

    public void selectFutureCells(GameContext context, CellImage currentCell, Array<CellImage> selectedFutureStepCells) {
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
