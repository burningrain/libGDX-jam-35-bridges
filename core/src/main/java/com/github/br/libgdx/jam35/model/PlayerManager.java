package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class PlayerManager {

    private Array<Player> players = new Array<>();
    private Player currentPlayer;

    private Player winner;

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void deletePlayer(Player player) {
        players.add(player);
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentPlayer(int number) {
        this.currentPlayer = players.get(number);
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public void start() {
        if (players.isEmpty()) {
            throw new IllegalStateException("players must be registered");
        }
        if (players.size <= 1) {
            throw new IllegalStateException("players size must be grower then 1");
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void clear() {
        currentPlayer = null;
        players.clear();
    }

    public int getPlayersCount() {
        return players.size;
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId);
    }

}
