package com.github.br.libgdx.jam35;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.ui.GameFieldScreen;
import com.github.br.libgdx.jam35.ui.GameType;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private GameContext context;
    private GameFieldScreen gameFieldScreen;

    @Override
    public void create() {
        FitViewport fitViewport = new FitViewport(1024, 768);
        context = createGameContext(fitViewport);
        loadAssets(context.getAssetManager());

        context.getGameModel().initEmptyGrid();

        gameFieldScreen = new GameFieldScreen(context, GameType.RUNTIME);
        setScreen(gameFieldScreen);
    }

    private GameContext createGameContext(Viewport viewport) {
        return new GameContext(
            new GameModel(),
            viewport,
            new AssetManager(new InternalFileHandleResolver(), true)
        );
    }

    private void loadAssets(AssetManager assetManager) {
        assetManager.load(Res.CELL, Texture.class);
        assetManager.load(Res.FUTURE_CELL, Texture.class);
        assetManager.load(Res.SELECTED_CELL, Texture.class);
        //assetManager.load(Res.SKIN, Skin.class);

        assetManager.finishLoading();
    }

    @Override
    public void dispose() {
        super.dispose();

        AssetManager assetManager = context.getAssetManager();
        assetManager.unload(Res.CELL);
        assetManager.unload(Res.FUTURE_CELL);
        assetManager.unload(Res.SELECTED_CELL);
    }

}
