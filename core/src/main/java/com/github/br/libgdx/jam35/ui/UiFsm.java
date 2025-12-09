package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;

public class UiFsm {

    public enum UiFsmStateType {
        SELECT_CELL_FROM,
        SELECT_CELL_TO,
        STEP
    }

    private CellImage from;
    private final Array<CellImage> selectedFutureStepCells = new Array<>(4);

    private UiFsmStateType currentFsmState = UiFsmStateType.SELECT_CELL_FROM;

    private final GameContext context;
    private final GameFieldUi gameFieldUi;

    public UiFsm(GameFieldUi gameFieldUi, GameContext context) {
        this.gameFieldUi = gameFieldUi;
        this.context = context;
    }

    public void reset() {
        deselectCurrentCells();
        currentFsmState = UiFsmStateType.SELECT_CELL_FROM;
    }

    public void handle(CellImage currentCell) {
        CellImageType type = currentCell.getType();

        if (currentFsmState == UiFsmStateType.SELECT_CELL_FROM) {
            selectCellFromAction(currentCell, type);
        } else if (currentFsmState == UiFsmStateType.SELECT_CELL_TO) {
            switch (type) {
                case NONE:
                    selectCellFromAction(currentCell, type);
                    break;
                case SELECTED:
                    deselectCurrentSelectedCellAction();
                    break;
                case FUTURE_STEP:
                    doStep(currentCell);
                    break;
            }
        }
    }

    private void doStep(CellImage to) {
        if (gameFieldUi.isOurCell(to)) {
            return;
        }

        GameModel gameModel = context.getGameModel();
        Cell modelFrom = from.getModel();
        Cell modelTo = to.getModel();

        deselectCurrentCells();

        currentFsmState = UiFsmStateType.STEP;
        gameModel.step(modelFrom, modelTo);
        currentFsmState = UiFsmStateType.SELECT_CELL_FROM;
    }

    private void deselectCurrentSelectedCellAction() {
        deselectCurrentCells();
        currentFsmState = UiFsmStateType.SELECT_CELL_FROM;
    }

    private void selectCellFromAction(CellImage currentCell, CellImageType type) {
        // валидация
        if (!gameFieldUi.isOurCell(currentCell)) {
            return;
        }
        if (type != CellImageType.NONE) {
            return;
        }

        deselectCurrentCells();
        gameFieldUi.select(currentCell);
        gameFieldUi.selectFutureCells(context, currentCell, selectedFutureStepCells);
        from = currentCell;

        currentFsmState = UiFsmStateType.SELECT_CELL_TO;
    }

    private void deselectCurrentCells() {
        gameFieldUi.deselect(from);
        gameFieldUi.deselectFutureCells(selectedFutureStepCells);
        selectedFutureStepCells.clear();
        from = null;
    }

}
