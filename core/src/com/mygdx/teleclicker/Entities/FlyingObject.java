package com.mygdx.teleclicker.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.teleclicker.Enums.FlyingObjectTypeEnum;
import com.mygdx.teleclicker.TeleClicker;

/**
 * Created by Senpai on 10.07.2016.
 */
public class FlyingObject extends Image {

    public final static String MONEY = "img/skins/flying_object/cash_1.png";
    public final static String PASSIVE = "img/skins/flying_object/diamond_1.png";
    public final static String MONEY_DOWN = "img/skins/flying_object/bomb_1.png";
    public final static String PASSIVE_DOWN = "img/skins/flying_object/jew_greedy.png";

    private final static int WIDHT = 70;
    private final static int HEIGHT = 70;

    private final static int STARTING_X1 = 0;
    private final static int STARTING_X2 = TeleClicker.WIDTH + WIDHT;
    private final static int STARTING_Y = -100;

    private int starting_x, ending_x;
    private int starting_y, ending_y;

    private TeleClicker game;
    private FlyingObjectTypeEnum type;

    public FlyingObject(FlyingObjectTypeEnum type, TeleClicker game) {
        super(new Texture(getTextureString(type)));

        this.type = type;
        this.game = game;

        this.setOrigin(WIDHT / 2, HEIGHT / 2);
        this.setSize(WIDHT, HEIGHT);

        // starting position
        randomizeStartingAndEndingCoord();
        this.setPosition(starting_x, starting_y);

        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {

                reactOnClick();
                return super.touchDown(event, x, y, pointer, button);
            }


        });
    }

    private void randomizeStartingAndEndingCoord() {
        if (MathUtils.randomBoolean()) { //UP or DOWN
            starting_y = MathUtils.randomBoolean() ? -90 : TeleClicker.HEIGHT;
            ending_y = TeleClicker.HEIGHT - starting_y;
            starting_x = MathUtils.random(0, TeleClicker.WIDTH);
            ending_x = MathUtils.random(0, TeleClicker.WIDTH);
        } else {
            starting_x = MathUtils.randomBoolean() ? -90 : TeleClicker.WIDTH;
            ending_x = TeleClicker.WIDTH - starting_x;
            starting_y = MathUtils.random(-70, TeleClicker.HEIGHT);
            ending_y = MathUtils.random(-70, TeleClicker.HEIGHT);
        }
    }

    private void reactOnClick() {
        switch (type) {
            case MONEY:
                game.getScoreService().addPoints(50);
                game.getSoundService().playCashRegisterSound();
                break;
            case MONEY_DOWN:
                game.getScoreService().addPoints(-50);
                game.getSoundService().playBombExplosionSound();
                break;
            case PASSIVE:
                game.getScoreService().addPassiveIncome(5);
                game.getSoundService().playPopSound();
                break;
            case PASSIVE_DOWN:
                game.getScoreService().addPassiveIncome(-5);
                game.getSoundService().playEvillaughJewSound();
                break;
        }
        FlyingObject.this.remove();
    }

    private static String getTextureString(FlyingObjectTypeEnum type) {
        switch (type) {
            case MONEY:
                return MONEY;
            case MONEY_DOWN:
                return MONEY_DOWN;
            case PASSIVE:
                return PASSIVE;
            case PASSIVE_DOWN:
                return PASSIVE_DOWN;
        }
        return "";
    }

    public void flyLikeHell() {

        final int MARGIN = 100;
        final int MAX_SPINS = 6;
        final int MIN_MOVED_TIME = 1;
        final int MAX_MOVED_TIME = 10;
        int rand_point_x1 = MathUtils.random(0, TeleClicker.WIDTH );
        int rand_point_y1 = MathUtils.random(0, TeleClicker.HEIGHT);
        int rand_move_time_1 = MathUtils.random(MIN_MOVED_TIME, MAX_MOVED_TIME);
        int rand_rotate_1 = MathUtils.random(-MAX_SPINS*360, MAX_SPINS*360);

        Action a = Actions.parallel(
                Actions.moveTo(rand_point_x1, rand_point_y1, rand_move_time_1),
                Actions.rotateBy(rand_rotate_1, rand_move_time_1)
        );

        int rand_move_time_2 = MathUtils.random(MIN_MOVED_TIME, MAX_MOVED_TIME);
        int rand_rotate_2 = MathUtils.random(-MAX_SPINS*360, MAX_SPINS*360);

        Action b = Actions.parallel(
                Actions.moveTo(ending_x, ending_y, rand_move_time_2),
                Actions.rotateBy(rand_rotate_2, rand_move_time_2)
        );

        Action c = Actions.run(new Runnable() {

            @Override
            public void run() {
                FlyingObject.this.remove();
            }
        });


        this.addAction(Actions.sequence(a, b, c));
    }
}
