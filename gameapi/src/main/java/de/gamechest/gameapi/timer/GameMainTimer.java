package de.gamechest.gameapi.timer;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameState;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMainTimer extends GameTimer {

    public GameMainTimer(int seconds) {
        super(GameAPI.getAPI().generateGameDefaultId("GameMainTimer"), seconds);
    }

    @Override
    public void start() {
        GameAPI.getAPI().setGameState(GameState.INGAME);
        super.start();
    }

    @Override
    public void end() {
        GameAPI.getAPI().setGameState(GameState.END);
        super.end();
    }
}
