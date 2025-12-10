package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class StepResolver {

    private final Validator validator;

    public StepResolver(Validator validator) {
        this.validator = validator;
    }

    //TODO правильно вычислять возможные ячейки и ходы
    public Array<Cell> getPossibleStepsForCell(Grid grid, Cell currentCell) {
        Array<Cell> result = new Array<>();

        int currentX = currentCell.getX();
        int currentY = currentCell.getY();

        // налево
        addStepIfPossible(currentX - 1, currentY, grid, currentCell, result);
        addJumpIfPossible(currentX - 2, currentY, grid, currentCell, result);
        // направо
        addStepIfPossible(currentX + 1, currentY, grid, currentCell, result);
        addJumpIfPossible(currentX + 2, currentY, grid, currentCell, result);
        // вверх
        addStepIfPossible(currentX, currentY + 1, grid, currentCell, result);
        addJumpIfPossible(currentX, currentY + 2, grid, currentCell, result);
        // вниз
        addStepIfPossible(currentX, currentY - 1, grid, currentCell, result);
        addJumpIfPossible(currentX, currentY - 2, grid, currentCell, result);

        return result;
    }

    private void addStepIfPossible(int x, int y, Grid grid,
                                   Cell currentCell, Array<Cell> result) {
        if (!validator.isAbleToStep(grid, currentCell, x, y)) {
            return;
        }

        Cell[][] cells = grid.getGrid();
        result.add(cells[x][y]);
    }

    private void addJumpIfPossible(int x, int y, Grid grid,
                                   Cell currentCell, Array<Cell> result) {
        if (!validator.isAbleToJump(grid, currentCell, x, y)) {
            return;
        }

        Cell[][] cells = grid.getGrid();
        result.add(cells[x][y]);
    }

}
