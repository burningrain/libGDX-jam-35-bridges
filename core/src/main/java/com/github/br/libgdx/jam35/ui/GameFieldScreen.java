package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.br.libgdx.jam35.GameContext;
import com.github.br.libgdx.jam35.Res;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.GameModel;
import com.github.br.libgdx.jam35.model.Grid;

public class GameFieldScreen implements Screen, GameModel.Listener {

    private GameContext context;

    private Stage stage;
    private Skin skin;

    public GameFieldScreen(GameContext context) {
        this.context = context;
    }

    @Override
    public void show() {
        stage = new Stage(context.getViewport());
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        update(context.getGameModel());
        context.getGameModel().addListener(this);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void update(GameModel model) {
        Grid modelGrid = model.getGrid();
        if (Grid.NULL_OBJECT == modelGrid) {
            return;
        }

        Cell[][] grid = modelGrid.getGrid();

        int padding = 32;
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        float cellSize = 64 + padding;
        float leftX = (width - cellSize * grid.length + padding) / 2f;
        float leftY = (height - cellSize * grid[0].length + padding) / 2f;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                createCell(grid[i][j], leftX + i * cellSize, leftY + j * cellSize);
            }
        }
    }

    private void createCell(Cell cell, float x, float y) {
        AssetManager assetManager = context.getAssetManager();
        Texture cellTexture = assetManager.get(Res.CELL);

        Image image = new Image(cellTexture);
        image.setPosition(x, y);
        stage.addActor(image);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;

        stage.getViewport().update(width, height);
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
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

}
