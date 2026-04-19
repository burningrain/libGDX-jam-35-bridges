package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.br.libgdx.jam35.model.round.RoundQueueManager;
import com.github.br.libgdx.jam35.model.step.ClearCellStep;
import com.github.br.libgdx.jam35.model.step.MoveStep;

public class FourPlayerStepHandler implements StepHandler {

    private final GameModel gameModel;
    private final PlayerManager playerManager;
    private final RoundQueueManager roundManager;

    private Player gambitPlayer = null; // игрок, начавший серию жертв

    public FourPlayerStepHandler(GameModel gameModel, PlayerManager playerManager, RoundQueueManager roundManager) {
        this.gameModel = gameModel;
        this.playerManager = playerManager;
        this.roundManager = roundManager;
    }

    @Override
    public void doStep(Cell from, Cell to) {
        Grid grid = gameModel.getGrid();
        gameModel.validationStep(grid, from, to);

        WasJump wasJump = new WasJump();
        Array<Cell> possibleStepsForCell = gameModel.getPossibleStepsForCell(from, wasJump);
        gameModel.validateStepTo(to, possibleStepsForCell); // проверяем, что сходили туда, куда можно сходить

        Player currentPlayer = playerManager.getCurrentPlayer();
        gameModel.validateNeedToJump(currentPlayer, wasJump); // проверяем, нужно ли бить

        gameModel.addLastStep(currentPlayer, from, to, wasJump.wasJump);
        gameModel.addStepToLog(new MoveStep(currentPlayer, from.copy(), to.copy(), wasJump.wasJump));
        from.setPlayer(Player.NULL_PLAYER);
        to.setPlayer(currentPlayer);
        if (wasJump.wasJump) {
            Cell midCell = gameModel.getMidCell(grid, from, to);
            Player victim = midCell.getPlayer();
            midCell.setPlayer(Player.NULL_PLAYER);
            gameModel.addStepToLog(new ClearCellStep(midCell.copy()));
            gameModel.addPlayersPoints(currentPlayer, victim);
        }
        gameModel.notifyListeners();

        // проверяем условия окончания игры
        // TODO лучше завести счетчики на каждого игрока и уменьшать счетчики при боях
        ObjectSet<Player> activePlayers = playerManager.getActivePlayersInTheGame(grid);
        if (activePlayers.size == 1) {
            playerManager.setWinner(activePlayers.iterator().next());
            gameModel.notifyListeners();
            return;
        } else if (activePlayers.size < playerManager.getPlayersCount()) {
            OrderedMap<Player, Integer> playersPoints = gameModel.getPlayersPoints();
            ObjectMap.Entry<Player, Integer> startEntry = playersPoints.iterator().next();
            Player winner = startEntry.key;
            int maxPoints = startEntry.value;
            for (ObjectMap.Entry<Player, Integer> entry : playersPoints.iterator()) {
                if (maxPoints < entry.value) {
                    maxPoints = entry.value;
                    winner = entry.key;
                }
            }
            playerManager.setWinner(winner);
            gameModel.notifyListeners();
            return;
        }

        // если был прыжок, то смотрим следующий ход - прыжок или нет
        // если прыжок ТОЙ ЖЕ ШАШКОЙ!!!, мы в середине удара находимся
        if (wasJump.wasJump && wasJump.currentCell == from) {
            // если идет удар той же шашкой(!), то ход не переходит к следующему игроку
            // а текущий продолжает свой удар
            WasJump willNextStepJump = new WasJump();
            Array<Cell> futureJump = gameModel.getPossibleStepsForCell(to, willNextStepJump);
            if (!futureJump.isEmpty() && willNextStepJump.currentCell == to) {
                // если всего один вариант, то делаем прыжок автоматически
                if (futureJump.size == 1) {
                    Cell nextCellAfterJump = futureJump.get(0);
                    this.doStep(to, nextCellAfterJump);
                }
                return;
            }
        }

        // проверяем, есть ли противник, которому надо бить твою шашку
        int id = currentPlayer.getId();
        int playersCount = playerManager.getPlayersCount();
        WasJump gambitJump = getGambitJump(playersCount, id);
        if (gambitJump != null) {
            Player donorPlayer = gambitJump.midCell.getPlayer();
            if (id == donorPlayer.getId()) {
                Player recipient = gambitJump.currentCell.getPlayer();
                int currentStep = roundManager.getCurrentStep();
                roundManager.insertNextStep(currentStep + 1, recipient.getId(), true);
                if (gambitPlayer == null) {
                    gambitPlayer = donorPlayer;
                    roundManager.insertNextStep(currentStep + 2, donorPlayer.getId(), true);
                }
            }
        } else {
            gambitPlayer = null;
        }

        int nextPlayer;
        do {
            roundManager.goToNextStep();
            nextPlayer = roundManager.getCurrentId();
        } while (wasJump.wasJump && currentPlayer.getId() == nextPlayer);

        playerManager.setCurrentPlayer(roundManager.getCurrentId());
    }

    private WasJump getGambitJump(int playersCount, int id) {
        for (int playerId = 0; playerId < playersCount; playerId++) {
            if (playerId == id) {
                continue;
            }

            Player player = playerManager.getPlayer(playerId);
            WasJump wasJump = gameModel.getWasJump(gameModel.getGrid(), player);
            if (wasJump != null && wasJump.wasJump && wasJump.midCell != null) {
                return wasJump;
            }
        }
        return null;
    }

    @Override
    public void doComputerStep(Player player) {
        Grid grid = gameModel.getGrid();
        Array<ComputerStepVariants> variants = gameModel.getVariants(grid, player);

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
            int toIndex = (possibleSteps.size - 1 == 0) ? 0 : MathUtils.random.nextInt(possibleSteps.size - 1);
            from = computerStepVariant.getCell();
            to = possibleSteps.get(toIndex);
        }

        doStep(from, to);
    }

}
