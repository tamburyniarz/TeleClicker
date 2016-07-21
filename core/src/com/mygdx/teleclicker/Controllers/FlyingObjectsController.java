package com.mygdx.teleclicker.Controllers;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.teleclicker.Entities.FlyingObject;
import com.mygdx.teleclicker.Enums.FlyingObjectTypeEnum;
import com.mygdx.teleclicker.TeleClicker;

/**
 * Created by Senpai on 10.07.2016.
 */
public class FlyingObjectsController {
    private static final int RANDOM_TICK_INTERVAL = 5;
    private int spawnTime;
    private final int MIN_SPAWN_TIME_INTERVAL = 0;
    private final int MAX_SPAWN_TIME_INTERVAL = 10;

    public FlyingObjectsController(TeleClicker game, Stage stage){
        init(game, stage);
    }

    private void init(final TeleClicker game, final Stage stage) {
        randomizeSpawnTime();

        Timer.schedule(new Timer.Task() {

            @Override
            public void run() {

                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {

                        addRandomFlyingObjectToStage(game, stage);
                        randomizeSpawnTime();

                    }
                }, spawnTime);
            }
        }, 0, RANDOM_TICK_INTERVAL);
    }

    private void randomizeSpawnTime() {
        spawnTime = MathUtils.random(MIN_SPAWN_TIME_INTERVAL, MAX_SPAWN_TIME_INTERVAL);
    }

    private void addRandomFlyingObjectToStage(TeleClicker game, Stage stage){
        FlyingObject flyingObject = null;

        switch (MathUtils.random(0,3)){
            case 0:
                flyingObject = new FlyingObject(FlyingObjectTypeEnum.MONEY, game);
                break;
            case 1:
                flyingObject = new FlyingObject(FlyingObjectTypeEnum.MONEY_DOWN, game);
                break;
            case 2:
                flyingObject = new FlyingObject(FlyingObjectTypeEnum.PASSIVE, game);
                break;
            case 3:
                flyingObject = new FlyingObject(FlyingObjectTypeEnum.PASSIVE_DOWN, game);
                break;
        }

        stage.addActor(flyingObject);
        flyingObject.flyLikeHell();
    }
}
