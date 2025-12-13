package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

public class Cell implements Json.Serializable {

    private int x, y;
    private Player player;

    public Cell() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell cell = (Cell) o;
        return x == cell.x && y == cell.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public Cell copy() {
        Cell copy = new Cell();
        copy.setX(this.getX());
        copy.setY(this.getY());
        copy.setPlayer(this.getPlayer());
        return copy;
    }

    @Override
    public void write(Json json) {
        json.writeValue("x", x);
        json.writeValue("y", y);
        json.writeValue("player", player, Player.class);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.x = json.readValue("x", int.class, 0, jsonData);
        this.y = json.readValue("y", int.class, 0, jsonData);
        this.player = json.readValue("player", Player.class, jsonData);
    }

}
