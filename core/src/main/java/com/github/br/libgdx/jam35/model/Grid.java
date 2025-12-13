package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Grid implements Json.Serializable {

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

    @Override
    public void write(Json json) {
        json.writeValue("grid", grid, Cell[][].class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.grid = json.readValue("grid", Cell[][].class, jsonData);
    }

}
