package de.gamechest.gameapi.countdown;

import de.gamechest.BountifulAPI;
import de.gamechest.gameapi.Callback;
import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import de.gamechest.gameapi.GameState;
import de.gamechest.gameapi.task.GameTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ByteList on 22.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameCountdown extends GameDefault {

    private GameState gameState;
    private GameTask gameTask;
    @Getter
    private int startTime, currentTime;
    @Getter @Setter
    private String message, title, subTitle;

    private Runnable runnable;
    private Callback<ArrayList<Player>> runAtEnd, runBeforeNotify;
    @Getter @Setter
    private GameCountdownNotifyType notifyType;
    @Getter
    private ArrayList<Player> players = new ArrayList<>();

    private int[] notifyTimes;

    public GameCountdown(GameState gameState, int startTime, GameCountdownNotifyType notifyType, int[] notifyTimes, String message) {
        this(gameState, startTime, notifyType, notifyTimes, message, "", "");
    }

    public GameCountdown(GameState gameState, int startTime, GameCountdownNotifyType notifyType, int[] notifyTimes, String message, String title, String subTitle) {
        super(GameAPI.getAPI().generateGameDefaultId("GameCountdown"));
        this.gameState = gameState;
        this.currentTime = startTime;
        this.startTime = startTime;
        this.notifyType = notifyType;
        this.notifyTimes = notifyTimes;
        this.message = message;
        this.title = title;
        this.subTitle = subTitle;

        this.runnable = ()-> {
            if(this.currentTime > 0) {
                this.currentTime--;
                for (int i : this.notifyTimes) {
                    if(i == this.currentTime) sendNotify();
                }
            } else {
                end();
            }
        };
    }

    public boolean startIf(boolean b) {
        if(b) start();

        return b;
    }

    public void start() {
        if(this.gameTask != null) this.gameTask = GameAPI.getAPI().runStateRepeatingSync(gameState, this.runnable, 20L);
    }

    public void pause() {
        if(this.gameTask != null) {
            GameAPI.getAPI().cancelTask(this.gameTask);
            this.gameTask = null;
        }
    }

    public void end() {
        pause();
        this.currentTime = 0;
        this.runAtEnd.run(this.players);
    }

    public void restart() {
        end();
        this.currentTime = this.startTime;
        start();
    }

    private void sendNotify() {
        // §7Das Spiel beginnt in §e%seconds%§7 Sekunde%n%
        String msg = replace(this.message), t = replace(this.title), st = replace(this.subTitle);

        switch (this.notifyType) {
            case CHAT:
                this.players.forEach(player -> player.sendMessage(msg));
                break;
            case ACTION_BAR:
                this.players.forEach(player -> BountifulAPI.sendActionBar(player, msg));
                break;
            case TITLE:
                this.players.forEach(player -> BountifulAPI.sendTitle(player, 5, 20, 5, t, st));
                break;
        }

        this.runBeforeNotify.run(this.players);
    }


    public void runAtEnd(Callback<ArrayList<Player>> runnable) {
        this.runAtEnd = runnable;
    }

    public void runBeforeNotify(Callback<ArrayList<Player>> runnable) {
        this.runBeforeNotify = runnable;
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    private String replace(String string) {
        return string.replace("%seconds%", String.valueOf(this.currentTime)).replace("%n%", (this.currentTime != 1 ? "n" : ""));
    }
}
