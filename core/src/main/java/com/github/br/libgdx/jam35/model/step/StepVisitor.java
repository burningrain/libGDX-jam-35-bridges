package com.github.br.libgdx.jam35.model.step;

public interface StepVisitor {

    void visit(MoveStep moveStep);

    void visit(ClearCellStep clearCellStep);

}
