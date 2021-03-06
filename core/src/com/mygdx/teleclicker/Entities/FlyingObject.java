package com.mygdx.teleclicker.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.teleclicker.Enums.FlyingObjectTypeEnum;
import com.mygdx.teleclicker.Service.ScoreService;
import com.mygdx.teleclicker.Service.SoundService;
import com.mygdx.teleclicker.TeleClicker;

/**
 * Created by Senpai on 22.07.2016.
 */
public class FlyingObject extends Image{

    private final static int WIDHT = 70;
    private final static int HEIGHT = 70;

    private final static int STARTING_X1 = 0;
    private final static int STARTING_X2 = TeleClicker.WIDTH + WIDHT;
    private final static int STARTING_Y = -100;

    private int starting_x, ending_x;
    private int starting_y, ending_y;

    private FlyingObjectTypeEnum type;

    public FlyingObject(FlyingObjectTypeEnum type) {
        super(type.getTexture());

        this.type = type;

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
                ScoreService.getInstance().addPoints(50);
                SoundService.getInstance().playCashRegisterSound();
                break;
            case MONEY_DOWN:
                ScoreService.getInstance().addPoints(-50);
                SoundService.getInstance().playBombExplosionSound();
                break;
            case PASSIVE:
                ScoreService.getInstance().addPointsPerSec(5);
                SoundService.getInstance().playPopSound();
                break;
            case PASSIVE_DOWN:
                ScoreService.getInstance().addPointsPerSec(-5);
                SoundService.getInstance().playEvillaughJewSound();
                break;
        }
        FlyingObject.this.remove();
    }
    public void Fly() {

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
