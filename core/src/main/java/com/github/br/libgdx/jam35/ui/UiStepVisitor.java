package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.model.Player;
import com.github.br.libgdx.jam35.model.step.ClearCellStep;
import com.github.br.libgdx.jam35.model.step.MoveStep;
import com.github.br.libgdx.jam35.model.step.StepVisitor;
import com.github.br.libgdx.jam35.ui.utils.Bridge;
import com.github.br.libgdx.jam35.ui.utils.GameFieldUi;

public class UiStepVisitor implements StepVisitor {

    private final GameFieldUi gameFieldUi;

    private Runnable next;
    private final Array<StepAction> stepActions = new Array<>();

    public UiStepVisitor(GameFieldUi gameFieldUi) {
        this.gameFieldUi = gameFieldUi;
    }

    public void visit(MoveStep moveStep) {
        stepActions.add(new BridgeAction(gameFieldUi, moveStep));
    }

    public void visit(ClearCellStep clearCellStep) {
        stepActions.add(new CleanCellAction(gameFieldUi, clearCellStep));
    }

    public void act(float delta) {
        if (!stepActions.isEmpty()) {
            StepAction stepAction = stepActions.get(0);
            boolean isEnd = stepAction.execute();
            if (isEnd) {
                stepActions.removeIndex(0);
            }

            if (stepActions.isEmpty()) {
                this.next.run();
                this.next = null;
            }
        }
    }

    public void executeWhenStepQueueIsEnd(Runnable next) {
        this.next = next;
    }

    private interface StepAction {
        boolean  execute();
    }

    private static class BridgeAction implements StepAction {

        private final GameFieldUi gameFieldUi;
        private final MoveStep moveStep;

        private Bridge bridge;

        public BridgeAction(GameFieldUi gameFieldUi, MoveStep moveStep) {
            this.moveStep = moveStep;
            this.gameFieldUi = gameFieldUi;
        }

        @Override
        public boolean execute() {
            if (bridge == null) {
                bridge = gameFieldUi.createBridge(
                    moveStep.getFrom(),
                    moveStep.getTo(),
                    moveStep.getCurrentPlayer().getPlayerColorType()
                );
                gameFieldUi.addToField(bridge);
            }

            boolean animationEnd = bridge.isAnimationEnd();
            if (animationEnd) {
                bridge.remove();

                gameFieldUi.changeCellType(moveStep.getFrom(), Player.NULL_PLAYER);
                gameFieldUi.changeCellType(moveStep.getTo(), moveStep.getFrom().getPlayer());
            }
            return animationEnd;
        }
    }

    private static class CleanCellAction implements StepAction {

        private final GameFieldUi gameFieldUi;
        private final ClearCellStep clearCellStep;

        public CleanCellAction(GameFieldUi gameFieldUi, ClearCellStep clearCellStep) {
            this.gameFieldUi = gameFieldUi;
            this.clearCellStep = clearCellStep;
        }

        @Override
        public boolean execute() {
            gameFieldUi.changeCellType(clearCellStep.getMidCell(), Player.NULL_PLAYER);
            return true;
        }
    }

}
