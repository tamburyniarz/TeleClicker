package com.mygdx.teleclicker.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.awt.*;

/**
 * Created by Senpai on 10.07.2016.
 */
public class GameLabel extends Label {

    public enum FontType{
        ARIAL, TIMES_NEW_ROMAN
    }

    private final static String TNR_SRC = "fonts/TimesNewRoman";
    private final static String ARIAL_SRC = "fonts/Arial";

    public GameLabel() {
        this(FontType.TIMES_NEW_ROMAN);
    }

    public GameLabel(FontType fontType) {
        super("", prepareLabelStyle(fontType));
    }

    private static LabelStyle prepareLabelStyle(FontType fontType) {
        String fontDir = getDirToFont(fontType);
        BitmapFont font = new BitmapFont(Gdx.files.internal(fontDir + ".fnt"), Gdx.files.internal(fontDir + ".png"), false);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK;

        return labelStyle;
    }

    private static String getDirToFont(FontType fontType){
        String dir = "";
        switch (fontType) {
            case TIMES_NEW_ROMAN:
                dir = TNR_SRC;
                break;
            case ARIAL:
                dir = ARIAL_SRC;
                break;
        }
        return dir;
    }
}
