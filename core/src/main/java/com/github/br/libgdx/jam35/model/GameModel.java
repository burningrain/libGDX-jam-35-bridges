package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.br.libgdx.jam35.model.exception.IncorrectStepException;
import com.github.br.libgdx.jam35.model.exception.NeedToJumpException;
import com.github.br.libgdx.jam35.model.round.RoundGenerator;
import com.github.br.libgdx.jam35.model.round.RoundQueueManager;
import com.github.br.libgdx.jam35.model.step.Step;

import java.util.Comparator;
import java.util.HashMap;

public class GameModel {

    private final Array<Listener> listeners = new Array<>();
    private final GridLoader gridLoader = new GridLoader();
    private final Validator validator = new Validator();
    private final PlayerManager playerManager = new PlayerManager();
    private final RoundQueueManager roundQueueManager = new RoundQueueManager();
    private final LastStepService lastStepService = new LastStepService();
    private final StepResolver stepResolver = new StepResolver(validator, lastStepService);
    private final PointsManager pointsManager = new PointsManager();

    private Grid grid = Grid.NULL_OBJECT;
    private boolean isNew = true;

    private GameModeType gameMode;
    private StepHandler stepHandler;

    private final Array<Step> currentSteps = new Array<>();

    public void initEmptyGrid() {
        this.setGrid(createEmptyGrid());
    }

    public void start(GameModeType modeType) {
        playerManager.validatePlayersBeforeStart();
        setGameMode(modeType);
        updateCurrentGrid();
        pointsManager.initPlayersCheckerAmount(getGrid());
    }

    private void updateCurrentGrid() {
        HashMap<PlayerColorType, Player> playersMap = new HashMap<>();
        int playersCount = playerManager.getPlayersCount();
        for (int i = 0; i < playersCount; i++) {
            Player player = playerManager.getPlayer(i);
            playersMap.put(player.getPlayerColorType(), player);
        }

        Cell[][] cells = grid.getGrid();
        for (Cell[] column : cells) {
            for (Cell cell : column) {
                Player cellPlayer = cell.getPlayer();
                if (cellPlayer != Player.NULL_PLAYER) {
                    cell.setPlayer(playersMap.get(cellPlayer.getPlayerColorType()));
                }
            }
        }
    }

    public void reset() {
        grid = Grid.NULL_OBJECT;
        isNew = true;

        currentSteps.clear();
        playerManager.clear();
        lastStepService.clear();
        pointsManager.clear();
        roundQueueManager.clear();
    }

    public boolean isGameEnd() {
        return playerManager.getWinner() != null;
    }

    public Array<Step> pollCurrentSteps() {
        Array<Step> result = new Array<>(currentSteps);
        currentSteps.clear();
        return result;
    }

    public void loadGrid(String pathToLevel) {
        FileHandle level = Gdx.files.internal(pathToLevel);
        Grid newGrid = gridLoader.toGrid(new String(level.readBytes()));
        setNew(true);
        setGrid(newGrid);
    }

    public void saveGrid(String levelName) {
        String grid = gridLoader.fromGrid(this.grid);
        FileHandle level = Gdx.files.internal(levelName);
        level.writeString(grid, false);
    }

    public void doStep(Cell from, Cell to) {
        stepHandler.doStep(from, to);
    }

    public void doComputerStep(Player player) {
        stepHandler.doComputerStep(player);
    }

    void validateNeedToJump(Player currentPlayer, WasJump wasJump) {
        boolean isNeedToJump = isNeedToJump(grid, currentPlayer);
        if (isNeedToJump && !wasJump.wasJump) {
            // если прыгать нужно, а не прыгнули, значит ошибка
            throw new NeedToJumpException();
        }
    }

    void validateStepTo(Cell to, Array<Cell> possibleStepsForCell) {
        if (!possibleStepsForCell.contains(to, true)) {
            throw new IncorrectStepException(to);
        }
    }

    boolean isNeedToJump(Grid grid, Player currentPlayer) {
        Array<ComputerStepVariants> variants = getVariants(grid, currentPlayer);
        for (ComputerStepVariants variant : variants) {
            if (variant.getWasJump().wasJump) {
                return true;
            }
        }
        return false;
    }

    WasJump getWasJump(Grid grid, Player currentPlayer) {
        Array<ComputerStepVariants> variants = getVariants(grid, currentPlayer);
        for (ComputerStepVariants variant : variants) {
            if (variant.getWasJump().wasJump) {
                return variant.getWasJump();
            }
        }
        return null;
    }

    Array<ComputerStepVariants> getVariants(Grid grid, Player me) {
        Array<ComputerStepVariants> variants = new Array<>();
        Cell[][] cells = grid.getGrid();
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[0].length; y++) {
                Cell cell = cells[x][y];
                if (!me.equals(cell.getPlayer())) {
                    continue;
                }
                WasJump wasJump = new WasJump();
                Array<Cell> possibleSteps = getPossibleStepsForCell(cell, wasJump);
                variants.add(new ComputerStepVariants(cell, possibleSteps, wasJump));
            }
        }
        return variants;
    }

    void addLastStep(Player currentPlayer, Cell from, Cell to, boolean isWasJump) {
        lastStepService.addLastStep(currentPlayer, from, to, isWasJump);
    }

    public Array<Cell> getPossibleStepsForCell(Cell currentCell, WasJump wasJump) {
        return stepResolver.getPossibleStepsForCell(getCurrentPlayer(), grid, currentCell, wasJump);
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

    public Array<Player> getActivePlayersInTheGame() {
        ObjectSet<Player> activePlayers = playerManager.getActivePlayersInTheGame(getGrid());
        Array<Player> playersArray = new Array<>();
        for (Player player : activePlayers) {
            playersArray.add(player);
        }
        playersArray.sort(Comparator.comparingInt(Player::getId));

        return playersArray;
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

    private Grid createEmptyGrid() {
        Cell[][] cells = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = new Cell();
                cell.setX(i);
                cell.setY(j);
                cell.setPlayer(Player.NULL_PLAYER);
                cells[i][j] = cell;
            }
        }
        return new Grid(cells);
    }

    public void validationStep(Grid grid, Cell from, Cell to) {
        validator.validationStep(grid, from, to);
    }

    public void addStepToLog(Step step) {
        currentSteps.add(step);
    }

    public Cell getMidCell(Grid grid, Cell from, Cell to) {
        return stepResolver.getMidCell(grid, from, to);
    }

    public void setGameMode(GameModeType gameMode) {
        this.gameMode = gameMode;
        switch (gameMode) {
            case TWO_PLAYERS:
                this.stepHandler = new TwoPlayerStepHandler(this, playerManager);
                break;
            case FOUR_PLAYERS:
                this.stepHandler = new FourPlayerStepHandler(this, playerManager, roundQueueManager);
                break;
            default:
                throw new IllegalArgumentException("gameMode=[" + gameMode + "] isn't supported");
        }
    }

    public GameModeType getGameMode() {
        return gameMode;
    }

    public void addPlayersPoints(Player currentPlayer, Player victim) {
        pointsManager.addPoints(currentPlayer, victim);
    }

    public OrderedMap<Player, Integer> getPlayersPoints() {
        return pointsManager.getPlayersPoints();
    }

    public void setRoundGenerator(RoundGenerator roundGenerator) {
        roundQueueManager.setRoundGenerator(roundGenerator);
    }

    public void initRound() {
        roundQueueManager.init();
    }

    public void setActivePlayers(Array<Player> activePlayersInTheGame) {
        playerManager.setActivePlayers(activePlayersInTheGame);
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

    public void notifyListeners() {
        for (Listener listener : listeners) {
            listener.update(this);
        }
    }
    // observer

}
