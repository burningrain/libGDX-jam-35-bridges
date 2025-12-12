package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class ComputerStepVariants {

    private final Cell cell;
    private final Array<Cell> possibleSteps;
    private final WasJump wasJump;

    public ComputerStepVariants(Cell cell, Array<Cell> possibleSteps, WasJump wasJump) {
        this.cell = cell;
        this.possibleSteps = possibleSteps;
        this.wasJump = wasJump;
    }

    public Cell getCell() {
        return cell;
    }

    public Array<Cell> getPossibleSteps() {
        return possibleSteps;
    }

    public WasJump getWasJump() {
        return wasJump;
    }

}
