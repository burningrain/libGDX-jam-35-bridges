package com.github.br.libgdx.jam35.model;

import java.util.Objects;

public class Cell {

    private int x, y;
    private Player player;

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
}
