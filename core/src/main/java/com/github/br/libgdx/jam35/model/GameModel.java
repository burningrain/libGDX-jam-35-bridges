package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class GameModel {

    private final Array<Listener> listeners = new Array<>();
    private final GridLoader gridLoader = new GridLoader();
    private final Validator validator = new Validator();
    private final StepResolver stepResolver = new StepResolver(validator);
    private final PlayerManager playerManager = new PlayerManager();

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

    //TODO вернуть массив передвижений для UI
    public void step(Cell from, Cell to) {
        validator.validationStep(grid, from, to);

        //TODO игровая логика
        System.out.println("\nfrom [" + from.getX() + "; " + from.getY() + "], type [" + from.getType() + "]" +
            "\nto [" + to.getX() + "; " + to.getY() + "], type [" + to.getType() + "]");
    }

    public Array<Cell> getPossibleStepsForCell(Cell currentCell) {
        return stepResolver.getPossibleStepsForCell(grid, currentCell);
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
        notifyListeners();
    }

    public void addPlayer(Player player) {
        playerManager.addPlayer(player);
    }

    public void start() {
        playerManager.start();
    }

    // observer
    public interface Listener {
        void update(GameModel model);
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
    // observer

}
