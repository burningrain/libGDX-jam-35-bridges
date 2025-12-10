package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class PlayerManager {

    private Array<Player> players = new Array<>();
    private Player currentPlayer;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void deletePlayer(Player player) {
        players.add(player);
    }

    public void start() {
        if (players.isEmpty()) {
            throw new IllegalStateException("players must be registered");
        }
        if (players.size <= 1) {
            throw new IllegalStateException("players size must be grower then 1");
        }

        currentPlayer = players.get(0);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void clear() {
        currentPlayer = null;
        players.clear();
    }

}
