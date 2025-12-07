package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class GameModel {

    private final Array<Listener> listeners = new Array<>();

    private Grid grid = Grid.NULL_OBJECT;

    public void init() {
        this.setGrid(createGrid());
    }

    private Grid createGrid() {
        //TODO загрузка из json-а внешнего
        Cell[][] cells = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell();
                cell.setX(i);
                cell.setY(j);
                cell.setType(CellType.EMPTY);

                cells[i][j] = cell;
            }
        }

        return new Grid(cells);
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
        notifyListeners();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        Array.ArrayIterator<Listener> iterator = listeners.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == listener) {
                iterator.remove();
            }
        }
    }

    private void notifyListeners() {
        for (Listener listener : listeners) {
            listener.update(this);
        }
    }

    public interface Listener {
        void update(GameModel model);
    }

}
