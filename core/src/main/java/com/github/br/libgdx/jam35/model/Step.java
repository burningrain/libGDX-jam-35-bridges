package com.github.br.libgdx.jam35.model;

public class Step {

    private final Player currentPlayer;
    private final Cell from;
    private final Cell to;

    public Step(Player currentPlayer, Cell from, Cell to) {
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

}
