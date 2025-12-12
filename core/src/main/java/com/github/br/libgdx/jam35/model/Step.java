package com.github.br.libgdx.jam35.model;

public class Step {

    private final Cell from;
    private final Cell to;

    public Step(Cell from, Cell to) {
        this.from = from;
        this.to = to;
    }

    public Cell getFrom() {
        return from;
    }

    public Cell getTo() {
        return to;
    }

}
