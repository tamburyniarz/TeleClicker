package com.mygdx.teleclicker.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Senpai on 11.07.2016.
 */
public class CloseShopButton extends Button{
    public CloseShopButton(final IClickCallback callback) {
        super(new ButtonStyle());
        init(callback);
    }

    private void init(final IClickCallback callback) {
        this.setWidth(43);
        this.setHeight(43);
        this.setX(339);
        this.setY(661);
        this.setDebug(true);

        this.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                callback.onClick();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }
}