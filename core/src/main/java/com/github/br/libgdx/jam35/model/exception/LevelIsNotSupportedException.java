package com.github.br.libgdx.jam35.model.exception;

public class LevelIsNotSupportedException extends GameException {

    public LevelIsNotSupportedException(int levelNumber) {
        super("levelNumber is not supported: " + levelNumber);
    }

}
