package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.github.br.libgdx.jam35.model.step.ClearCellStep;
import com.github.br.libgdx.jam35.model.step.MoveStep;

public class TwoPlayerStepHandler implements StepHandler {

    private final GameModel gameModel;
    private final PlayerManager playerManager;

    public TwoPlayerStepHandler(GameModel gameModel, PlayerManager playerManager) {
        this.gameModel = gameModel;
        this.playerManager = playerManager;
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
        }

        // если был прыжок, то смотрим следующий ход - прыжок или нет
        // если прыжок ТОЙ ЖЕ ШАШКОЙ!!!, мы в середине удара находимся
        if (wasJump.wasJump && wasJump.currentCell == from) {
            // если идет удар той же шашкой(!), то ход не переходит к следующему игроку
            // а текущий продолжает свой удар
            WasJump willNextStepJump = new WasJump();
            Array<Cell> futureJump = gameModel.getPossibleStepsForCell(to, willNextStepJump);
            if (!futureJump.isEmpty()  && willNextStepJump.currentCell == to) {
                // если всего один вариант, то делаем прыжок автоматически
                if (futureJump.size == 1) {
                    Cell nextCellAfterJump = futureJump.get(0);
                    this.doStep(to, nextCellAfterJump);
                }
                return;
            }
        }

        // иначе переходим к следующему игроку
        playerManager.goToNextPlayer();
    }

    //TODO копипаста с вариантом на 4-х игроков
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
            Array<Cell> possibleSteps = null;
            ComputerStepVariants computerStepVariant = null;
            do {
                int variantIndex = (variants.size - 1 == 0) ? 0 : MathUtils.random.nextInt(variants.size - 1);
                computerStepVariant = variants.get(variantIndex);
                possibleSteps = computerStepVariant.getPossibleSteps();
            } while (possibleSteps.isEmpty());
            int toIndex = (possibleSteps.size - 1 == 0) ? 0 : MathUtils.random.nextInt(possibleSteps.size - 1);
            from = computerStepVariant.getCell();
            to = possibleSteps.get(toIndex);
        }

        doStep(from, to);
    }

}
