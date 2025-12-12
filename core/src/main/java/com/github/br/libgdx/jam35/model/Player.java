package com.github.br.libgdx.jam35.model;

import java.util.Objects;

public class Player {

    public static final Player NO_PLAYER = null;

    private final int id;
    private final PlayerColorType playerColorType;
    private final UserType userType;

    public Player(int id, PlayerColorType playerColorType, UserType userType) {
        this.id = id;
        this.playerColorType = playerColorType;
        this.userType = userType;
    }

    public PlayerColorType getPlayerType() {
        return playerColorType;
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
        return id == player.id && playerColorType == player.playerColorType && userType == player.userType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, playerColorType, userType);
    }
}
