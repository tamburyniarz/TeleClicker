package com.mygdx.teleclicker.Service;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.teleclicker.ui.GameLabel;

import java.util.concurrent.TimeUnit;

/**
 * Created by Senpai on 10.07.2016.
 */
public class ScoreService {
    public final static String GAME_SCORE = "com.mygdx.clicker.prefs.score";
    public final static String GAME_PASSIVE_INCOME = "com.mygdx.clicker.prefs.passive";
    public final static String GAME_SAVED_TIMESTAMP = "com.mygdx.clicker.prefs.timestamp";

    private float points;
    private float passiveIncome;
    private float pointsToAdd;

    private GameLabel scoreLabel;
    private GameLabel passiveIncomeLabel;

    private Preferences prefs;

    public ScoreService(Preferences prefs) {
        this.prefs = prefs;
        init();
    }

    public void printLabels(Stage stage) {
        printLabels(stage, false);
    }

    public void printLabels(Stage stage, boolean isShop) {
        if(isShop){
            scoreLabel.setPosition(10, 677);
            passiveIncomeLabel.setPosition(250, 677);
        } else {
            scoreLabel.setPosition(30, 670);
            passiveIncomeLabel.setPosition(30, 635);
        }
        stage.addActor(scoreLabel);
        stage.addActor(passiveIncomeLabel);
    }

    public void saveTimeStamp() {

    }

    private void init() {
        scoreLabel = new GameLabel();
        passiveIncomeLabel = new GameLabel();
        loadScore();
        calculateGainedPassiveIncome();

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                points += passiveIncome;
            }
        }, 1, 1);
    }

    private void calculateGainedPassiveIncome() {
        if (getSavedTimeStamp() > 0) {
            final float multiplier = 0.1f;
            long deleyTime = TimeUtils.millis() - getSavedTimeStamp();

            pointsToAdd = TimeUnit.MILLISECONDS.toSeconds(deleyTime) * multiplier * passiveIncome;
            points += pointsToAdd;
        }
    }

    public void addPoints(float pointsToAdd) {
        points += pointsToAdd;
        if (points < 0)
            points = 0;
    }

    public void addPoint() {
        points++;
    }

    public void resetGameScore() {
        points = 0.0f;
        passiveIncome = 0.0f;
    }

    public void updateScoreLabel(boolean isShop) {
        if(isShop){
            scoreLabel.setText("Erlangi: \n" +
                    "" + String.format("%.2f", points));
            passiveIncomeLabel.setText("Erl / sec: \n" +
                    "" + passiveIncome);
        } else {
            scoreLabel.setText("Erlangi: " + String.format("%.2f", points));
            passiveIncomeLabel.setText("Erl / sec: " + passiveIncome);
        }
    }

    public void addPassiveIncome(int value) {
        passiveIncome += value;
        if (passiveIncome < 0)
            passiveIncome = 0;
    }

    private void loadScore() {
        passiveIncome = prefs.getFloat(GAME_PASSIVE_INCOME);
        points = prefs.getFloat(GAME_SCORE);
    }

    public void saveCurrentGameState() {
        prefs.putFloat(GAME_SCORE, points);
        prefs.putFloat(GAME_PASSIVE_INCOME, passiveIncome);
        prefs.putLong(GAME_SAVED_TIMESTAMP, TimeUtils.millis());
        prefs.flush();
    }

    /**
     * ---------------------
     * getters and setters
     */

    private Long getSavedTimeStamp() {
        return prefs.getLong(GAME_SAVED_TIMESTAMP);
    }

    public float getPoints() {
        return points;
    }

    public float getPointsToAdd() {
        return pointsToAdd;
    }

    public void multiplierPoints(float multiplier) {
        points *= multiplier;
    }
}
