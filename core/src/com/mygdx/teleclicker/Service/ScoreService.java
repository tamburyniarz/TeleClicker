package com.mygdx.teleclicker.Service;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.teleclicker.Entities.Player;
import com.mygdx.teleclicker.Entities.PlayerStats;
import com.mygdx.teleclicker.Enums.DBStatusEnum;
import com.mygdx.teleclicker.Enums.ScreenEnum;
import com.mygdx.teleclicker.TeleClicker;

import java.util.concurrent.TimeUnit;

/**
 * Created by Senpai on 21.07.2016.
 */
public class ScoreService {
    public final static String GAME_SCORE = "com.mygdx.clicker.prefs.score";
    public final static String GAME_PASSIVE_INCOME = "com.mygdx.clicker.prefs.pointspersec";
    public final static String GAME_SAVED_TIMESTAMP = "com.mygdx.clicker.prefs.timestamp";
    public final static String GAME_POINTS_PER_CLICK = "com.mygdx.clicker.prefs.pointsperclick";
    public final static String GAME_PASSIVE_INCOME_TIME = "com.mygdx.clicker.prefs.passiveincometime";

    public final static String GAME_NO_CLICKS = "com.mygdx.clicker.prefs.numberofclicks";

    public final static String GAME_NO_POINTS_PER_CLICK_BUYS = "com.mygdx.clicker.prefs.pointsperclickbuys";
    public final static String GAME_NO_POINTS_PER_SEC_BUYS = "com.mygdx.clicker.prefs.pointspersecbuys";

    final HttpService httpService;

    private static ScoreService instance;

    private float points;
    private float pointsPerSec;
    private float pointsPerClick = 1.0f;
    private float passiveIncomeTimeInHour;

    private long delayTime;

    private long numberOfClicks;

    private int numberOfPointsPerClickPBuys;
    private int numberOfPointsPerSecBuys;

    private Preferences prefs;

    private DBStatusEnum loginStatus;
    private boolean isLoaded = false;
    private PlayerStats playerStats;

    private ScoreService() {
        this.httpService = new HttpService();
        this.prefs = TeleClicker.getPrefs();
        calculateGainedPassiveIncome();
        initTimer();
    }

    public void saveStatsOnServer() {
        System.out.println(Player.ID);
    }

    private void loadPlayerStatsFromServer(String id) {
        httpService.loadStatsRequest(id);
        System.out.println(httpService.getStatus().toString());

        final Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                System.out.println(httpService.getStatus().toString());
                if (httpService.getStatus() == DBStatusEnum.FAILED || httpService.getStatus() == DBStatusEnum.CANCELLED) {
                    loginStatus = httpService.getStatus();
                    timer.clear();
                } else {
                    if (httpService.getStatus() == DBStatusEnum.SUCCES) {
                        Json json = new Json();
                        playerStats = json.fromJson(PlayerStats.class, httpService.getResponsStr());

                        System.out.println(playerStats.toString());

                        loadStatsFromPlayerStats(playerStats);
                        isLoaded = true;

                        timer.clear();
                    }
                }
            }
        }, 0.5f, 1, 5);
    }

    private void loadStatsFromPlayerStats(PlayerStats pStats) {
        points = pStats.getPointsPerSec();
        pointsPerSec = pStats.getPointsPerSec();
        pointsPerClick = pStats.getPointsPerClick();
        passiveIncomeTimeInHour = pStats.getPassiveIncomeTimeInHour();

        numberOfClicks = pStats.getNumberOfClicks();
        numberOfPointsPerClickPBuys = pStats.getNumberOfPointsPerClickPBuys();
        numberOfPointsPerSecBuys = pStats.getNumberOfPointsPerSecBuys();
    }

    public void loadScore(String login, String password) {
        final HttpService loadHttp = new HttpService();
        loadHttp.loginRequest(login, password);

        final Timer logintimer = new Timer();
        logintimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                switch (loadHttp.getStatus()){
                    case SUCCES:
                        //Get player "id"
                        Json json = new Json();
                        PlayerStats pStat = json.fromJson(PlayerStats.class, loadHttp.getResponsStr());

                        DBStatusEnum statusEnum = DBStatusEnum.valueOf(pStat.getStatus());
                        System.out.println(statusEnum.toString());

                        switch (statusEnum){
                            case SUCCES:
                                loadStatsFromPlayerStats(pStat);
                                loginStatus = statusEnum;
                                ScreenService.getInstance().setScreen(ScreenEnum.GAMEPLAY);
                                logintimer.clear();
                                break;
                            default:
                                loginStatus = statusEnum;
                                logintimer.clear();
                                break;
                        }
                        break;
                    default:
                        loginStatus = loadHttp.getStatus();
                        System.out.println(loadHttp.getStatus().toString());
                        break;
                }
            }
        }, 0, 1);
    }

    private void loadPlayerStatsFromLocal() {
        playerStats.setId(-1);
        playerStats.setPoints(prefs.getFloat(GAME_SCORE));
        playerStats.setPointsPerSec(prefs.getFloat(GAME_PASSIVE_INCOME));
        playerStats.setPointsPerClick(prefs.getFloat(GAME_POINTS_PER_CLICK, 1));
        playerStats.setNumberOfClicks(prefs.getLong(GAME_NO_CLICKS));
        playerStats.setNumberOfPointsPerSecBuys(prefs.getInteger(GAME_NO_POINTS_PER_SEC_BUYS));
        playerStats.setNumberOfPointsPerClickPBuys(prefs.getInteger(GAME_NO_POINTS_PER_CLICK_BUYS));

        loadStatsFromPlayerStats(playerStats);
    }

    private void initTimer() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                points += pointsPerSec / 10;
            }
        }, 1, 0.1f);
    }

    public static ScoreService getInstance() {
        if (instance == null) {
            instance = new ScoreService();
        }
        return instance;
    }

    private void calculateGainedPassiveIncome() {
        final float multiplier = 0.1f;

        long delayTimeInSec = TimeUnit.MILLISECONDS.toSeconds(delayTime);
        long passiveIncomeTimeInSec = TimeUnit.HOURS.toSeconds((long) passiveIncomeTimeInHour);
        if (delayTimeInSec > passiveIncomeTimeInSec)
            delayTimeInSec = passiveIncomeTimeInSec;

        float pointsToAdd = delayTimeInSec * multiplier * pointsPerSec;
        points += pointsToAdd;
    }

    public void addPoints(float pointsToAdd) {
        points += pointsToAdd;
        if (points < 0)
            points = 0;
    }

    public void addPoint() {
        points += pointsPerClick;
    }

    public void addPointsPerClick(int add) {
        pointsPerClick += add;
    }

    public void resetGameScore() {
        points = 0.0f;
        pointsPerSec = 0.0f;
        pointsPerClick = 1.0f;
        passiveIncomeTimeInHour = 1.0f;

        numberOfPointsPerClickPBuys = 0;
        numberOfPointsPerSecBuys = 0;

        saveCurrentGameState();
    }

    public void addPointsPerSec(int value) {
        pointsPerSec += value;
        if (pointsPerSec < 0)
            pointsPerSec = 0;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void increseNumberOfClick() {
        numberOfClicks++;
    }

    public void increseNumberOfPointsPerClickBuys() {
        numberOfPointsPerClickPBuys++;
    }

    public void increseNumberOfPointsPerSecBuys() {
        numberOfPointsPerSecBuys++;
    }


    public void saveCurrentGameState() {
        prefs.putFloat(GAME_SCORE, points);
        prefs.putFloat(GAME_PASSIVE_INCOME, pointsPerSec);
        prefs.putFloat(GAME_POINTS_PER_CLICK, pointsPerClick);
        prefs.putFloat(GAME_PASSIVE_INCOME_TIME, passiveIncomeTimeInHour);
        prefs.putLong(GAME_SAVED_TIMESTAMP, TimeUtils.millis());

        prefs.putLong(GAME_NO_CLICKS, numberOfClicks);

        // Shop values
        prefs.putInteger(GAME_NO_POINTS_PER_CLICK_BUYS, numberOfPointsPerClickPBuys);
        prefs.putInteger(GAME_NO_POINTS_PER_SEC_BUYS, numberOfPointsPerSecBuys);

        prefs.flush();
    }

    /**
     * ---------------------
     * getters and setters
     */

    public DBStatusEnum getLoginStatus() {
        return loginStatus;
    }

    public long getNumberOfClicks() {
        return numberOfClicks;
    }

    public float getPoints() {
        return points;
    }

    public float getPointsPerSec() {
        return pointsPerSec;
    }

    public float getPointsPerClick() {
        return pointsPerClick;
    }

    public int getNumberOfPointsPerClickBuys() {
        return numberOfPointsPerClickPBuys;
    }

    public int getNumberOfPointsPerSecBuys() {
        return numberOfPointsPerSecBuys;
    }

    public void multiplierPoints(float multiplier) {
        points *= multiplier;
    }
}
