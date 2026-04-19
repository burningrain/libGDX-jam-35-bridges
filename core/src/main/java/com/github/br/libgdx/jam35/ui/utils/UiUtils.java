package com.github.br.libgdx.jam35.ui.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Player;
import com.github.br.libgdx.jam35.model.UserType;
import com.github.br.libgdx.jam35.ui.UiFsm;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UiUtils {

    private final AssetManager assetManager;

    public UiUtils(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public CellImage createCell(Cell cell, float x, float y) {
        TextureAtlas atlas = assetManager.get(Res.CLOUDS_AND_BRIDGES);

        CellImage image = new CellImage(
            cell,
            atlas.findRegion(Res.Cloud.EMPTY),
            atlas.findRegion(Res.Cloud.YELLOW),
            atlas.findRegion(Res.Cloud.VIOLET),
            atlas.findRegion(Res.Cloud.BLUE),
            atlas.findRegion(Res.Cloud.BROWN)
        );
        image.setPosition(x, y);

        return image;
    }

    public TextButton createButton(Skin skin, String title, int x, int y) {
        TextButton modeButton = new TextButton(title, skin);
        modeButton.setX(x);
        modeButton.setY(y);
        return modeButton;
    }

    public Window createWindow(
        Stage stage,
        Skin skin,
        String header,
        String buttonText,
        ChangeListener changeListener
        ) {
        Window window = new Window(header, skin);
        window.defaults().pad(4f);

        final TextButton button = new TextButton(buttonText, skin);
        button.pad(8f);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeListener.changed(event, actor);
                window.remove();
            }
        });
        window.add(button);
        window.pack();
        // We round the window position to avoid awkward half-pixel artifacts.
        // Casting using (int) would also work.
        window.setPosition(MathUtils.roundPositive(stage.getWidth() / 2f - window.getWidth() / 2f),
            MathUtils.roundPositive(stage.getHeight() / 2f - window.getHeight() / 2f));
        window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));

        stage.addActor(window);

        return window;
    }


    public Window createSettingsWindow(
        Stage stage,
        Skin skin,
        String header,
        GameModel gameModel,
        String buttonText,
        Consumer<Array<Player>> okChangeListener
    ) {
        Window window = new Window(header, skin, "settings");
        window.defaults().pad(4f);

        Table table = new Table();

        Array<Player> activePlayersInTheGame = gameModel.getActivePlayersInTheGame();
        for (Player player : activePlayersInTheGame) {
            CellImage cell = createCell(new Cell(), 818, 190);// разница по y в 50
            cell.setPlayerColor(player);
            table.add(cell);

            SelectBox<UserType> selectBox = new SelectBox<>(skin);
            selectBox.setItems(UserType.values());
            selectBox.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    player.setUserType(selectBox.getSelected());
                }
            });
            table.add(selectBox).spaceBottom(10);
            table.row();
        }

        final TextButton button = new TextButton(buttonText, skin);
        button.pad(8f);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                window.remove();
                okChangeListener.accept(activePlayersInTheGame);
            }
        });
        table.row();
        table.add(button).colspan(2);

        window.add(table);
        window.pack();
        // We round the window position to avoid awkward half-pixel artifacts.
        // Casting using (int) would also work.
        window.setPosition(MathUtils.roundPositive(stage.getWidth() / 2f - window.getWidth() / 2f),
            MathUtils.roundPositive(stage.getHeight() / 2f - window.getHeight() / 2f));
        window.addAction(Actions.sequence(Actions.alpha(0f), Actions.fadeIn(1f)));

        stage.addActor(window);

        return window;
    }

}
