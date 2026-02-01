package com.github.br.libgdx.jam35.ui.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.Player;

public class CellImage extends Image {

    private static final float ROTATION_SPEED = 30.0f;

    private TextureRegion emptyCloud;
    private TextureRegion yellowCloud;
    private TextureRegion violetCloud;
    private TextureRegion blueCloud;
    private TextureRegion brownCloud;

    private Cell model;

    private CellImageType type = CellImageType.NONE;


    public CellImage(
        Cell model,
        TextureRegion emptyCloud,
        TextureRegion yellowCloud,
        TextureRegion violetCloud,
        TextureRegion blueCloud,
        TextureRegion brownCloud
    ) {
        super(emptyCloud);
        this.model = model;

        this.emptyCloud = emptyCloud;
        this.yellowCloud = yellowCloud;
        this.violetCloud = violetCloud;
        this.blueCloud = blueCloud;
        this.brownCloud = brownCloud;

        this.setPlayerColor(model.getPlayer());

        this.setSize(this.emptyCloud.getRegionWidth(), this.emptyCloud.getRegionHeight());
        this.setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);
    }

    public void setSelectType(CellImageType type) {
        this.type = type;
    }

    public void setPlayerColor(Player player) {
        TextureRegion region = null;
        if (player == null) {
            region = emptyCloud;
        } else {
            switch (player.getPlayerColorType()) {
                case YELLOW:
                    region = yellowCloud;
                    break;
                case VIOLET:
                    region = violetCloud;
                    break;
                case BLUE:
                    region = blueCloud;
                    break;
                case BROWN:
                    region = brownCloud;
                    break;
            }
        }

        ((TextureRegionDrawable) getDrawable()).setRegion(region);
    }

    @Override
    public void act (float delta) {
        switch (this.type) {
            case SELECTED:
            case FUTURE_STEP:
                this.rotateBy(ROTATION_SPEED * delta);
                break;
        }
        super.act(delta);
    }

    public Cell getModel() {
        return model;
    }

    public void setModel(Cell model) {
        this.model = model;
    }

    public CellImageType getType() {
        return type;
    }

}
