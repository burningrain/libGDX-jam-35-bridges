package com.github.br.libgdx.jam35.model;

import java.io.Serializable;

public class Grid implements Serializable {

    public static final Grid NULL_OBJECT = new Grid(new Cell[0][0]);

    private Cell[][] grid;

    public Grid() {
    }

    public Grid(Cell[][] grid) {
        this.grid = grid;
    }

    public Cell[][] getGrid() {
        return grid;
    }

    public void setGrid(Cell[][] grid) {
        this.grid = grid;
    }

}
