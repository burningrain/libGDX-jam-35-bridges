package com.github.br.libgdx.jam35.model;

public class Validator {

    public void validationStep(Grid grid, Cell from, Cell to) {
        CellType fromType = from.getType();
        CellType toType = to.getType();
        boolean isNeedTrowException = false;
        if (!isCellExist(grid, to.getX(), to.getY()) || !isAbleToStep(grid, from, to.getX(), to.getY())) {
            isNeedTrowException = true;
        } else if (CellType.EMPTY == fromType || CellType.EMPTY != toType) {
            isNeedTrowException = true;
        }

        if (isNeedTrowException) {
            throw new IllegalArgumentException(
                "Incorrect step:" +
                    "\nfrom [" + from.getX() + ";" + from.getY() + "], type [" + fromType + "]" +
                    "\nto [" + to.getX() + "; " + to.getY() + "], type [" + toType + "]"
            );
        }
    }

    public boolean isCellExist(Grid grid, int toX, int toY) {
        if (Grid.NULL_OBJECT == grid) {
            return false;
        }

        Cell[][] cells = grid.getGrid();
        int maxX = cells.length - 1;
        int maxY = cells[0].length - 1;

        if (toX < 0 || toX > maxX) {
            return false;
        }

        if (toY < 0 || toY > maxY) {
            return false;
        }

        return true;
    }

    public boolean isAbleToStep(Grid grid, Cell currentCell, int x, int y) {
        if (CellType.EMPTY == currentCell.getType()) {
            return false;
        }
        if (!isCellExist(grid, x, y)) {
            return false;
        }

        Cell[][] cells = grid.getGrid();
        Cell toCell = cells[x][y];

        CellType fromType = currentCell.getType();
        CellType toType = toCell.getType();

        return CellType.EMPTY != fromType && CellType.EMPTY == toType;
    }

    public boolean isAbleToJump(Grid grid, Cell currentCell, int x, int y) {
        if (CellType.EMPTY == currentCell.getType()) {
            return false;
        }
        if (!isCellExist(grid, x, y)) {
            return false;
        }

        Cell[][] cells = grid.getGrid();
        Cell toCell = cells[x][y];

        Cell midCell = getMidCell(currentCell, toCell, cells);
        if (CellType.EMPTY != toCell.getType()) {
            return false;
        }

        if (CellType.EMPTY == midCell.getType()) {
            return false;
        }

        return currentCell.getType() != midCell.getType();
    }

    private static Cell getMidCell(Cell currentCell, Cell toCell, Cell[][] cells) {
        int midX;
        if (currentCell.getX() == toCell.getX()) {
           midX = currentCell.getX();
        } else if(currentCell.getX() < toCell.getX()) {
            midX = currentCell.getX() + 1;
        } else {
            midX = toCell.getX() + 1;
        }

        int midY;
        if (currentCell.getY() == toCell.getY()) {
            midY = currentCell.getY();
        } else if(currentCell.getY() < toCell.getY()) {
            midY = currentCell.getY() + 1;
        } else {
            midY = toCell.getY() + 1;
        }

        return cells[midX][midY];
    }

}
