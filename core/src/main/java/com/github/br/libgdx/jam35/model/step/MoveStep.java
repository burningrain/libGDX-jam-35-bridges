package com.github.br.libgdx.jam35.model.step;

import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.Player;

public class MoveStep implements Step {

    private final Player currentPlayer;
    private final Cell from;
    private final Cell to;

    public MoveStep(Player currentPlayer, Cell from, Cell to) {
        this.currentPlayer = currentPlayer;
        this.from = from;
        this.to = to;
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

    @Override
    public void visit(StepVisitor stepVisitor) {
        stepVisitor.visit(this);
    }

}
