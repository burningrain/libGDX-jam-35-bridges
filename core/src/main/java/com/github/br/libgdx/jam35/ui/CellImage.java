package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.github.br.libgdx.jam35.model.Cell;
import com.github.br.libgdx.jam35.model.CellType;

public class CellImage extends Image {

    private static final Color EMPTY_COLOR = new Color(1, 1, 1, 1);;
    private static final Color OUR_COLOR = new Color(0.78f, 0.53f, 0f, 1f);
    private static final Color ENEMY_COLOR = new Color(0.6f, 0.8f, 0.27f, 1f);

    private final TextureRegion cellTexture;
    private final TextureRegion selected;
    private final TextureRegion futureStep;

    private final Cell model;

    private CellImageType type = CellImageType.NONE;

    public CellImage(Cell model, TextureRegion defaultTexture, TextureRegion selected, TextureRegion futureStep) {
        super(defaultTexture);
        this.model = model;

        this.cellTexture = defaultTexture;
        this.selected = selected;
        this.futureStep = futureStep;

        this.setModelCellType(model.getType());
    }

    public void setSelectType(CellImageType type) {
        this.type = type;
        updateTexture(type);
    }

    private void updateTexture(CellImageType type) {
        TextureRegion region;
        switch (type) {
            case SELECTED:
                region = selected;
                break;
            case FUTURE_STEP:
                region = futureStep;
                break;
            case NONE:
            default:
                region = cellTexture;
                break;
        }
        ((TextureRegionDrawable) getDrawable()).setRegion(region);
    }

    public void setModelCellType(CellType type) {
        Color color = null;
        switch (type) {
            case EMPTY:
                color = EMPTY_COLOR;
                break;
            case WHITE_CELL:
                color = OUR_COLOR;
                break;
            case BLACK_CELL:
                color = ENEMY_COLOR;
                break;
        }
        setColor(color);
    }

    public Cell getModel() {
        return model;
    }

    public CellImageType getType() {
        return type;
    }

}
