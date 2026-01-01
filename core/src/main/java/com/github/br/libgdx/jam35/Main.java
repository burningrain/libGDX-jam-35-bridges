package com.github.br.libgdx.jam35;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.ui.*;
import com.ray3k.stripe.FreeTypeSkinLoader;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game implements ScreenLoader {

    private GameContext context;

    private MainMenuScreen mainMenuScreen;
    private GameFieldScreen quizScreen;
    private EditorScreen editorScreen;
    private GameFieldScreen2Players gameFieldScreen2Players;
    private GameFieldScreen2Players gameFieldScreen4Players;

    @Override
    public void create() {
        FitViewport fitViewport = new FitViewport(1024, 768);
        context = createGameContext(fitViewport);
        loadAssets(context.getAssetManager());

        context.getGameModel().initEmptyGrid();

        mainMenuScreen = new MainMenuScreen(context, this);
        quizScreen = new GameFieldScreen(context, GameType.QUIZ, this);
        editorScreen = new EditorScreen(context, this);
        gameFieldScreen2Players = new GameFieldScreen2Players(context, this);
        gameFieldScreen4Players = new GameFieldScreen4Players(context, this);

        loadMainMenu();
    }

    private GameContext createGameContext(Viewport viewport) {
        return new GameContext(
            new GameModel(),
            viewport,
            new AssetManager(new InternalFileHandleResolver(), true)
        );
    }

    private void loadAssets(AssetManager assetManager) {
        assetManager.setLoader(Skin.class, new FreeTypeSkinLoader(assetManager.getFileHandleResolver()));
        assetManager.setLoader(BitmapFont.class, new FreetypeFontLoader(assetManager.getFileHandleResolver()));

        assetManager.load(Res.CLOUDS_AND_BRIDGES, TextureAtlas.class);
        assetManager.load(Res.SKIN, Skin.class);

        assetManager.finishLoading();
    }

    @Override
    public void dispose() {
        super.dispose();

        AssetManager assetManager = context.getAssetManager();
        assetManager.unload(Res.CLOUDS_AND_BRIDGES);
        //assetManager.unload(Res.MAIN_MENU_SCREEN);
    }

    @Override
    public void loadMainMenu() {
        setScreen(mainMenuScreen);
    }

    @Override
    public void loadQuiz() {
        setScreen(quizScreen);
    }

    @Override
    public void loadEditor() {
        setScreen(editorScreen);
    }

    @Override
    public void load2Players() {
        setScreen(gameFieldScreen2Players);
    }

    @Override
    public void load4Players() {
        setScreen(gameFieldScreen4Players);
    }

}
