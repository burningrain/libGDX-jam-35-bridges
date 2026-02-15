package com.github.br.libgdx.jam35.model.step;

import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.Player;

public class MoveStep implements Step {

    private final Player currentPlayer;
    private final Cell from;
    private final Cell to;
    private final boolean isJump;

    public MoveStep(Player currentPlayer, Cell from, Cell to, boolean isJump) {
        this.currentPlayer = currentPlayer;
        this.from = from;
        this.to = to;
        this.isJump = isJump;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Cell getFrom() {
        return from;
    }

    public Cell getTo() {
        return to;
    }

    public boolean isJump() {
        return isJump;
    }

    @Override
    public void visit(StepVisitor uiStepVisitor) {
        uiStepVisitor.visit(this);
    }

}
