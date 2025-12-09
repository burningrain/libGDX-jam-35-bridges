package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class GameModel {

    private final Array<Listener> listeners = new Array<>();
    private final GridLoader gridLoader = new GridLoader();

    private Grid grid = Grid.NULL_OBJECT;
    private boolean isNew = true;

    public void init() {
        this.setGrid(createEmptyGrid());
    }

    public Grid createEmptyGrid() {
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

    // "levels/level0.json"
    public void loadGrid(String pathToLevel) {
        FileHandle level = Gdx.files.local(pathToLevel);
        Grid newGrid = gridLoader.toGrid(new String(level.readBytes()));
        setNew(true);
        setGrid(newGrid);
    }

    public void saveGrid(String levelName) {
        String grid = gridLoader.fromGrid(this.grid);
        FileHandle level = Gdx.files.local(levelName);
        level.writeString(grid, false);
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

    public void step(Cell from, Cell to) {
        validationStep(from, to);

        //TODO игровая логика
        System.out.println("\nfrom [" + from.getX() + "; " + from.getY() + "], type [" + from.getType() + "]" +
            "\nto [" + to.getX() + "; " + to.getY() + "], type [" + to.getType() + "]");
    }

    private void validationStep(Cell from, Cell to) {
        CellType fromType = from.getType();
        CellType toType = to.getType();
        boolean isNeedTrowException = false;
        if (!isAbleToStep(to.getX(), to.getY()) || from == to) {
            isNeedTrowException = true;
        } else if (CellType.OUR_CELL != fromType || CellType.EMPTY != toType) {
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

    public boolean isAbleToStep(int toX, int toY) {
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

    public interface Listener {
        void update(GameModel model);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
