package com.github.br.libgdx.jam35.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Bridge extends Image {

    public Bridge(Animation animation) {
        super(new AnimatedDrawable(animation));
        this.setSize(this.getWidth(), this.getHeight());
        this.setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);
    }

    public boolean isAnimationEnd() {
        AnimatedDrawable animatedDrawable = (AnimatedDrawable) getDrawable();
        return animatedDrawable.isAnimationEnd();
    }

}
