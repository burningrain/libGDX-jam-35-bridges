package com.github.br.libgdx.jam35.model.round;

public class RoundStep {
    public int id;
    public boolean isOutOfQueue;
    public boolean isRoundEnd;

    public RoundStep(int id, boolean isOutOfQueue, boolean isRoundEnd) {
        this.id = id;
        this.isOutOfQueue = isOutOfQueue;
        this.isRoundEnd = isRoundEnd;
    }
}
