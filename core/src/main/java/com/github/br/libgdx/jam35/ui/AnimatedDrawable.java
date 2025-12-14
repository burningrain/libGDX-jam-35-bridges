package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class AnimatedDrawable extends TextureRegionDrawable {

    private final Animation animation;
    private TextureRegion keyFrame;
    private float stateTime = 0;

    public AnimatedDrawable(Animation animation) {
        this.animation = animation;
        TextureRegion key = (TextureRegion) animation.getKeyFrame(0);

        this.setLeftWidth(key.getRegionWidth() / 2);
        this.setRightWidth(key.getRegionWidth() / 2);
        this.setTopHeight(key.getRegionHeight() / 2);
        this.setBottomHeight(key.getRegionHeight() / 2);
        this.setMinWidth(key.getRegionWidth());
        this.setMinHeight(key.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        stateTime += Gdx.graphics.getDeltaTime();
        keyFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
        setRegion(keyFrame);

        super.draw(batch, x, y, width, height);
    }

    @Override
    public void draw(
        Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX,
        float scaleY, float rotation
    ) {
        stateTime += Gdx.graphics.getDeltaTime();
        keyFrame = (TextureRegion) animation.getKeyFrame(stateTime, true);
        setRegion(keyFrame);

        super.draw(batch, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
    }

    public boolean isAnimationEnd() {
        return animation.isAnimationFinished(stateTime);
    }

}
