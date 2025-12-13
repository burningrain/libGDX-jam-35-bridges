package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

public class GameModel {

    private final Array<Listener> listeners = new Array<>();
    private final GridLoader gridLoader = new GridLoader();
    private final Validator validator = new Validator();
    private final StepResolver stepResolver = new StepResolver(validator);
    private final PlayerManager playerManager = new PlayerManager();

    private Grid grid = Grid.NULL_OBJECT;
    private boolean isNew = true;

    private final Array<Step> currentSteps = new Array<>();

    public void init() {
        this.setGrid(createEmptyGrid());
    }

    public void start() {
        playerManager.setCurrentPlayer(0);
        playerManager.start();
    }

    public boolean isGameEnd() {
        return playerManager.getWinner() != null;
    }


    public Grid createEmptyGrid() {
        Cell[][] cells = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell();
                cell.setX(i);
                cell.setY(j);
                cell.setPlayer(Player.NO_PLAYER);
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

    public void step(Cell from, Cell to) {
        validator.validationStep(grid, from, to);

        // todo не забыть удалить
        String stepLog = "\nfrom [" + from.getX() + "; " + from.getY() + "], type [" + from.getPlayer() + "]" +
            "\nto [" + to.getX() + "; " + to.getY() + "], type [" + to.getPlayer() + "]";
        System.out.println(stepLog);

        WasJump wasJump = new WasJump();
        Array<Cell> possibleStepsForCell = getPossibleStepsForCell(from, wasJump);
        // проверяем, что сходили куда можно сходить
        if (!possibleStepsForCell.contains(to, true)) {
            throw new IllegalArgumentException("incorrect to=[" + to.getX() + "; " + to.getY() + "], type [" + to.getPlayer() + "]");
        }

        Player currentPlayer = playerManager.getCurrentPlayer();
        boolean isNeedJump = isNeedToJump(grid, currentPlayer);
        if (isNeedJump && !wasJump.wasJump) {
            // если прыгать нужно, а не прыгнули, значит ошибка
            throw new IllegalArgumentException("need to jump");
        }

        currentSteps.add(new Step(currentPlayer, from.copy(), to.copy()));
        from.setPlayer(Player.NO_PLAYER);
        to.setPlayer(currentPlayer);
        if (wasJump.wasJump) {
            wasJump.midCell.setPlayer(Player.NO_PLAYER);
        }

        notifyListeners();

        ObjectSet<Player> activePlayers = getActivePlayersInTheGame(grid);
        if (activePlayers.size == 1) {
            playerManager.setWinner(activePlayers.iterator().next());
            notifyListeners();
            return;
        }

        // если был прыжок, то смотрим следующий ход - прыжок или нет
        // если прыжок, мы в середине удара находимся
        if (wasJump.wasJump) {
            isNeedJump = isNeedToJump(grid, currentPlayer);
            if (isNeedJump) {
                // если идет удар, то ход не переходит к следующему игроку
                // а текущий продолжает свой удар
                return;
            }
        }

        // иначе переходим к следующему игроку
        Player nextPlayer = playerManager.goToNextPlayer();
        while (!isGameEnd() && UserType.COMPUTER == nextPlayer.getUserType()) {
            calculateComputerStep(nextPlayer);
            nextPlayer = playerManager.getCurrentPlayer();
        }
    }

    private ObjectSet<Player> getActivePlayersInTheGame(Grid grid) {
        ObjectSet<Player> result = new ObjectSet<>();
        Cell[][] cells = grid.getGrid();
        for (Cell[] rows : cells) {
            for (Cell row : rows) {
                Player player = row.getPlayer();
                if (player != null) {
                    result.add(player);
                }
            }
        }

        return result;
    }

    private boolean isNeedToJump(Grid grid, Player currentPlayer) {
        Array<ComputerStepVariants> variants = getVariants(grid, currentPlayer);
        for (ComputerStepVariants variant : variants) {
            if (variant.getWasJump().wasJump) {
                return true;
            }
        }
        return false;
    }

    public void calculateComputerStep(Player player) {
        Array<ComputerStepVariants> variants = getVariants(grid, player);

        Cell from = null;
        Cell to = null;
        boolean isNeedJump = false;
        for (ComputerStepVariants variant : variants) {
            if (variant.getWasJump().wasJump) {
                from = variant.getCell();
                to = variant.getPossibleSteps().get(0);
                isNeedJump = true;
                break;
            }
        }
        if (!isNeedJump) {
            int variantIndex = (variants.size - 1 == 0) ? 0 : MathUtils.random.nextInt(variants.size - 1);
            ComputerStepVariants computerStepVariant = variants.get(variantIndex);

            Array<Cell> possibleSteps = computerStepVariant.getPossibleSteps();
            int toIndex = (possibleSteps.size - 1 == 0)? 0 : MathUtils.random.nextInt(possibleSteps.size - 1);
            from = computerStepVariant.getCell();
            to = possibleSteps.get(toIndex);
        }

        step(from, to);
    }

    private Array<ComputerStepVariants> getVariants(Grid grid, Player me) {
        Array<ComputerStepVariants> variants = new Array<>();
        Cell[][] cells = grid.getGrid();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                Cell cell = cells[x][y];
                if (cell.getPlayer() != me) {
                    continue;
                }
                WasJump wasJump = new WasJump();
                Array<Cell> possibleSteps = getPossibleStepsForCell(cell, wasJump);
                variants.add(new ComputerStepVariants(cell, possibleSteps, wasJump));
            }
        }
        return variants;
    }

    public Array<Cell> getPossibleStepsForCell(Cell currentCell, WasJump wasJump) {
        return stepResolver.getPossibleStepsForCell(grid, currentCell, wasJump);
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

    public void addPlayer(PlayerColorType playerColorType, UserType userType) {
        playerManager.addPlayer(playerColorType, userType);
    }

    public void setCurrentPlayer(int playerNumber) {
        playerManager.setCurrentPlayer(playerNumber);
    }

    public Player getCurrentPlayer() {
        return playerManager.getCurrentPlayer();
    }

    public int getPlayersCount() {
        return playerManager.getPlayersCount();
    }

    public Player getPlayer(int playerId) {
        return playerManager.getPlayer(playerId);
    }

    public Player getWinnerPlayer() {
        return playerManager.getWinner();
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


    public Array<Step> pollCurrentSteps() {
        Array<Step> result = new Array<>(currentSteps);
        currentSteps.clear();
        return result;
    }

}
