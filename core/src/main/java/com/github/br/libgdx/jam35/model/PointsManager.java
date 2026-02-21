package com.github.br.libgdx.jam35.model;

import com.badlogic.gdx.utils.OrderedMap;

public class PointsManager {

    private final OrderedMap<Player, Integer> checkerAmounts = new OrderedMap<>();
    private final OrderedMap<Player, Integer> playersPoints = new OrderedMap<>();

    public void initPlayersCheckerAmount(Grid grid) {
        Cell[][] cells = grid.getGrid();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                Player player = cell.getPlayer();
                if (player == null) {
                    continue;
                }

                Integer amount = checkerAmounts.get(player);
                if (amount == null) {
                    amount = 0;
                }
                amount = amount + 1;

                checkerAmounts.put(player, amount);
            }
        }

        for (OrderedMap.Entry<Player, Integer> entry : checkerAmounts) {
            playersPoints.put(entry.key, 0);
        }

    }

    public void addPoints(Player currentPlayer, Player victim) {
        Integer victimAmount = checkerAmounts.get(victim);
        checkerAmounts.put(victim, victimAmount - 1);

        Integer points = playersPoints.get(currentPlayer);
        if (points == null) {
            points = 0;
        }
        points = points + 1;
        playersPoints.put(currentPlayer, points);
    }

    public OrderedMap<Player, Integer> getPlayersPoints() {
        return playersPoints;
    }

    public void clear() {
        checkerAmounts.clear();
        playersPoints.clear();
    }

}
