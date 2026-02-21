package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.ObjectMap;

import java.util.Objects;

public class LastStepService {

    private final ObjectMap<Player, Cell> fromMap = new ObjectMap<>();
    private final ObjectMap<Player, Cell> toMap = new ObjectMap<>();
    private final ObjectMap<Player, Boolean> booleanMap = new ObjectMap<>();

    public void addLastStep(Player player, Cell from, Cell to, boolean isWasJump) {
        fromMap.put(player, from);
        toMap.put(player, to);
        booleanMap.put(player, isWasJump);
    }

    public boolean isReverseLastStep(Player player, Cell from, Cell to) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);

        Cell lastFrom = fromMap.get(player);
        Cell lastTo = toMap.get(player);

        Boolean isWasJump = booleanMap.get(player);
        if (isWasJump != null && isWasJump) {
            return false; // возвращаться назад в прыжке разрешено (сделано для варианта 4-ех игроков)
        }

        return to.equals(lastFrom) && from.equals(lastTo);
    }

    public void clear() {
        fromMap.clear();
        toMap.clear();
        booleanMap.clear();
    }

}
