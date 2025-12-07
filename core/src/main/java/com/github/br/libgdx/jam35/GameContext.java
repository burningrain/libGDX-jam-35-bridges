package com.github.br.libgdx.jam35;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.br.libgdx.jam35.model.GameModel;

public class GameContext {

    private final GameModel gameModel;
    private final Viewport viewport;
    private final AssetManager assetManager;

    public GameContext(GameModel gameModel, Viewport viewport, AssetManager assetManager) {
        this.gameModel = gameModel;
        this.viewport = viewport;
        this.assetManager = assetManager;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

}
