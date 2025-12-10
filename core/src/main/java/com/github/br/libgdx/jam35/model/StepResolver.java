package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class StepResolver {

    private final Validator validator;

    public StepResolver(Validator validator) {
        this.validator = validator;
    }

    //TODO правильно вычислять возможные ячейки и ходы
    public Array<Cell> getPossibleStepsForCell(PlayerType playerType, Grid grid, Cell currentCell) {
        Array<Cell> result = new Array<>();

        int currentX = currentCell.getX();
        int currentY = currentCell.getY();
        Cell[][] cells = grid.getGrid();

        Cell toCell;
        // налево
        if (validator.isCellExist(grid, currentX - 1, currentY)) {
            toCell = cells[currentX - 1][currentY];
            if (validator.isAbleToStep(playerType, currentCell, toCell)) {
                result.add(toCell);
            }
        }

        // направо
        if (validator.isCellExist(grid, currentX + 1, currentY)) {
            toCell = cells[currentX + 1][currentY];
            if (validator.isAbleToStep(playerType, currentCell, toCell)) {
                result.add(toCell);
            }
        }

        // вверх
        if (validator.isCellExist(grid, currentX, currentY + 1)) {
            toCell = cells[currentX][currentY + 1];
            if (validator.isAbleToStep(playerType, currentCell, toCell)) {
                result.add(toCell);
            }
        }

        // вниз
        if (validator.isCellExist(grid, currentX, currentY - 1)) {
            toCell = cells[currentX][currentY - 1];
            if (validator.isAbleToStep(playerType, currentCell, toCell)) {
                result.add(toCell);
            }
        }

        return result;
    }

}
