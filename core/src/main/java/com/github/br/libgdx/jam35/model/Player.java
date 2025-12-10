package com.github.br.libgdx.jam35.model;

import java.util.Objects;

public class Player {

    private final int id;
    private final PlayerType playerType;
    private final UserType userType;

    public Player(int id, PlayerType playerType, UserType userType) {
        this.id = id;
        this.playerType = playerType;
        this.userType = userType;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public UserType getUserType() {
        return userType;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id && playerType == player.playerType && userType == player.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerType, userType);
    }
}
