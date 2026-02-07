package com.github.br.libgdx.jam35.ui.screen;

import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.ScreenLoader;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.PlayerColorType;
import com.github.br.libgdx.jam35.model.UserType;

public class GameFieldScreen4Players extends GameFieldScreen2Players {

    public GameFieldScreen4Players(GameContext context, ScreenLoader screenLoader) {
        super(context, screenLoader);
    }

    protected void setLevelSettings(GameModel gameModel) {
        gameModel.loadGrid("levels/4_players.json");
        gameModel.addPlayer(PlayerColorType.BLUE, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.BROWN, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.VIOLET, UserType.HUMAN);
        gameModel.addPlayer(PlayerColorType.YELLOW, UserType.HUMAN);
        gameModel.setCurrentPlayer(0);
    }

}
