package com.github.br.libgdx.jam35.model;

public class Validator {

    public void validationStep(Grid grid, Cell from, Cell to, PlayerType playerType) {
        CellType fromType = from.getType();
        CellType toType = to.getType();
        boolean isNeedTrowException = false;
        if (!isCellExist(grid, to.getX(), to.getY()) || !isAbleToStep(playerType, from, to)) {
            isNeedTrowException = true;
        } else if (CellType.WHITE_CELL != fromType || CellType.EMPTY != toType) {
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

    public boolean isAbleToStep(PlayerType playerType, Cell from, Cell to) {
        CellType fromType = from.getType();
        CellType toType = to.getType();

        if (PlayerType.WHITE == playerType) {
            return CellType.WHITE_CELL == fromType && CellType.EMPTY == toType;
        } else {
            return CellType.BLACK_CELL == fromType && CellType.EMPTY == toType;
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

}
