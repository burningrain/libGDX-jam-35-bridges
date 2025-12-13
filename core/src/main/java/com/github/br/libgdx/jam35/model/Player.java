package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Objects;

public class Player implements Json.Serializable {

    public static final Player NULL_PLAYER = null;

    private int id;
    private PlayerColorType playerColorType;
    private UserType userType;

    public Player(){}

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

    public void setId(int id) {
        this.id = id;
    }

    public void setPlayerColorType(PlayerColorType playerColorType) {
        this.playerColorType = playerColorType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public PlayerColorType getPlayerColorType() {
        return playerColorType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Player{" +
            "id=" + id +
            ", playerColorType=" + playerColorType +
            ", userType=" + userType +
            '}';
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("playerColorType", playerColorType);
        json.writeValue("userType", userType);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.id = json.readValue("id", int.class, 0, jsonData);

        this.playerColorType = json.readValue("playerColorType", PlayerColorType.class, jsonData);
        this.userType = json.readValue("userType", UserType.class, jsonData);
    }

}
