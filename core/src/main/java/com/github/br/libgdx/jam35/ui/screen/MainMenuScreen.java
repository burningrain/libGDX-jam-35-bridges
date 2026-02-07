package com.github.br.libgdx.jam35.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.ScreenLoader;
import com.ray3k.stripe.scenecomposer.SceneComposerStageBuilder;

public class MainMenuScreen implements GameScreen {

    private final ScreenLoader screenLoader;
    private final GameContext context;
    private final Stage stage;
    private final Skin skin;

    public MainMenuScreen(GameContext context, ScreenLoader screenLoader) {
        this.context = context;
        this.screenLoader = screenLoader;

        stage = new Stage(context.getViewport());
        skin = context.getAssetManager().get(Res.SKIN);

        SceneComposerStageBuilder builder = new SceneComposerStageBuilder();
        builder.build(stage, skin, Gdx.files.internal(Res.MAIN_MENU_SCREEN));

        TextButton quizButton = stage.getRoot().findActor("quizBtn");
        quizButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenLoader.loadQuiz();
            }
        });

        TextButton editorButton = stage.getRoot().findActor("editorBtn");
        editorButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenLoader.loadEditor();
            }
        });

        TextButton twoPlayersButton = stage.getRoot().findActor("2playersBtn");
        twoPlayersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenLoader.load2Players();
            }
        });

        TextButton forPlayersButton = stage.getRoot().findActor("4playersBtn");
        forPlayersButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                screenLoader.load4Players();
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(51f / 255f, 204f / 255f, 255f / 255f, 1f);
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public InputProcessor getStage() {
        return stage;
    }
}
