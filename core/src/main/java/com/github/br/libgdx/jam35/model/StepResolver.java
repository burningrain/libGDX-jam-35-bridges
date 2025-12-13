package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class StepResolver {

    private final Validator validator;

    public StepResolver(Validator validator) {
        this.validator = validator;
    }

    //TODO правильно вычислять возможные ячейки и ходы
    public Array<Cell> getPossibleStepsForCell(Grid grid, Cell currentCell, WasJump wasJump) {
        Array<Cell> result = new Array<>();

        int currentX = currentCell.getX();
        int currentY = currentCell.getY();

        // бить обязательно. бой имеет приоритет над ходом
        boolean isJumpLeft = addJumpIfPossible(currentX - 2, currentY, grid, currentCell, result); // налево
        boolean isJumpRight = addJumpIfPossible(currentX + 2, currentY, grid, currentCell, result); // направо
        boolean isJumpUp = addJumpIfPossible(currentX, currentY + 2, grid, currentCell, result); // вверх
        boolean isJumpDown = addJumpIfPossible(currentX, currentY - 2, grid, currentCell, result); // вниз

        Cell[][] cells = grid.getGrid();
        if (isJumpLeft) {
            wasJump.wasJump = true;
            wasJump.midCell = cells[currentX - 1][currentY];
            wasJump.currentCell = currentCell;
            return result;
        }
        if (isJumpRight) {
            wasJump.wasJump = true;
            wasJump.midCell = cells[currentX + 1][currentY];
            wasJump.currentCell = currentCell;
            return result;
        }
        if (isJumpUp) {
            wasJump.wasJump = true;
            wasJump.midCell = cells[currentX][currentY + 1];
            wasJump.currentCell = currentCell;
            return result;
        }
        if (isJumpDown) {
            wasJump.wasJump = true;
            wasJump.midCell = cells[currentX][currentY - 1];
            wasJump.currentCell = currentCell;
            return result;
        }

        addStepIfPossible(currentX - 1, currentY, grid, currentCell, result); // налево
        addStepIfPossible(currentX + 1, currentY, grid, currentCell, result); // направо
        addStepIfPossible(currentX, currentY + 1, grid, currentCell, result); // вверх
        addStepIfPossible(currentX, currentY - 1, grid, currentCell, result); // вниз

        return result;
    }

    private boolean addStepIfPossible(int x, int y, Grid grid,
                                      Cell currentCell, Array<Cell> result) {
        if (!validator.isAbleToStep(grid, currentCell, x, y)) {
            return false;
        }

        Cell[][] cells = grid.getGrid();
        result.add(cells[x][y]);

        return true;
    }

    private boolean addJumpIfPossible(int x, int y, Grid grid,
                                      Cell currentCell, Array<Cell> result) {
        if (!validator.isAbleToJump(grid, currentCell, x, y)) {
            return false;
        }

        Cell[][] cells = grid.getGrid();
        result.add(cells[x][y]);

        return true;
    }

}
