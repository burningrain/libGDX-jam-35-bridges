package com.github.br.libgdx.jam35.model.exception;

import com.github.br.libgdx.jam35.model.Cell;

public class IncorrectStepException extends GameException {

    public IncorrectStepException(Cell to) {
        super("incorrect to=[" + to.getX() + "; " + to.getY() + "], type [" + to.getPlayer() + "]");
    }

}
