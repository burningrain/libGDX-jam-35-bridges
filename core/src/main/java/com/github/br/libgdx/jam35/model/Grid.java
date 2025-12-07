package com.github.br.libgdx.jam35.model;

public class Grid {

    public static final Grid NULL_OBJECT = new Grid(new Cell[0][0]);

    private final Cell[][] grid;

    public Grid(Cell[][] grid) {
        this.grid = grid;
    }

    public Cell[][] getGrid() {
        return grid;
    }

}
