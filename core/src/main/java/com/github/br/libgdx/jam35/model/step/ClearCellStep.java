package com.github.br.libgdx.jam35.model.step;

import com.github.br.libgdx.jam35.model.Cell;

public class ClearCellStep implements Step {

    private final Cell midCell;

    public ClearCellStep(Cell midCell) {
        this.midCell = midCell;
    }

    public Cell getMidCell() {
        return midCell;
    }

    @Override
    public void visit(StepVisitor stepVisitor) {
        stepVisitor.visit(this);
    }

}
