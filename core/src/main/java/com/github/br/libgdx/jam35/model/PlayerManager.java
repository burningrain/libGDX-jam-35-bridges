package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Array;

public class PlayerManager {

    private final Array<Player> players = new Array<>();

    private Player currentPlayer;
    private int currentPlayerNumber = 0;

    private Player winner;

    public Player goToNextPlayer() {
        int nextPlayer = currentPlayerNumber + 1;
        if (nextPlayer == players.size) {
            currentPlayerNumber = 0;
        } else {
            currentPlayerNumber++;
        }
        setCurrentPlayer(currentPlayerNumber);
        return getCurrentPlayer();
    }

    public void addPlayer(PlayerColorType playerColorType, UserType userType) {
        players.add(new Player(currentPlayerNumber, playerColorType, userType));
        currentPlayerNumber++;
    }

    public void deletePlayer(int number) {
        players.removeIndex(number);
    }

    public void setCurrentPlayer(int number) {
        this.currentPlayer = players.get(number);
        this.currentPlayerNumber = number;
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
        winner = null;
        players.clear();
    }

    public int getPlayersCount() {
        return players.size;
    }

    public Player getPlayer(int playerId) {
        return players.get(playerId);
    }

}
