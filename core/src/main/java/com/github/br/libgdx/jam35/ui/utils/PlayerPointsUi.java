package com.github.br.libgdx.jam35.ui.utils;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Player;

import java.util.Comparator;

public class PlayerPointsUi {

    private final UiUtils uiUtils;
    private final GameContext context;

    private final ObjectMap<Player, CellImage> cellsMap = new ObjectMap<>();
    private final ObjectMap<Player, Label> labelsMap = new ObjectMap<>();

    public PlayerPointsUi(UiUtils uiUtils, GameContext context) {
        this.uiUtils = uiUtils;
        this.context = context;
    }

    public void init(int paddingX, int paddingUp, Stage stage, Skin skin) {
        if (isAlreadyInit()) {
            clean();
        }

        GameModel gameModel = context.getGameModel();
        OrderedMap<Player, Integer> playersPoints = gameModel.getPlayersPoints();

        Array<Player> keys = playersPoints.orderedKeys();
        keys.sort(Comparator.comparingInt(Player::getId));

        int counter = 0;
        float cellSize = GameFieldUi.CELL_SIZE + GameFieldUi.CELL_PADDING_RIGHT;
        for (int i = keys.size - 1; i >= 0; i--) {
            Player player = keys.get(i);
            Integer value = playersPoints.get(player);

            float paddingY = cellSize + paddingUp + counter * cellSize;

            CellImage cellImage = uiUtils.createCell(new Cell(), paddingX, paddingY);
            cellImage.setPlayerColor(player);
            cellsMap.put(player, cellImage);
            stage.addActor(cellImage);

            Label label = new Label("" + value, skin);
            label.setX(paddingX + 80);
            label.setY(paddingY - 27);
            labelsMap.put(player, label);
            stage.addActor(label);

            counter++;
        }
    }

    public void update(ObjectMap<Player, Integer> playersPoints) {
        for (ObjectMap.Entry<Player, Label> entry : labelsMap) {
            Integer points = playersPoints.get(entry.key);
            entry.value.setText(points);
        }
    }

    private void clean() {
        for (ObjectMap.Entry<Player, CellImage> entry : cellsMap) {
            entry.value.remove();
        }
        cellsMap.clear();

        for (ObjectMap.Entry<Player, Label> entry : labelsMap) {
            entry.value.remove();
        }
        labelsMap.clear();
    }

    private boolean isAlreadyInit() {
        return !(cellsMap.isEmpty() && labelsMap.isEmpty());
    }

}
