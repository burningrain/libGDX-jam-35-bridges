package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class GridLoader {

    private final Json json = new Json(JsonWriter.OutputType.json);

    public Grid toGrid(String grid) {
        return json.fromJson(Grid.class, grid);
    }

    public String fromGrid(Grid grid) {
        return json.toJson(grid);
    }

}
