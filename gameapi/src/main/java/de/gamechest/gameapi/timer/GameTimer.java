package de.gamechest.gameapi.timer;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import de.gamechest.gameapi.event.GameTimerFinishedEvent;
import de.gamechest.gameapi.event.GameTimerTickEvent;
import de.gamechest.gameapi.task.GameTask;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameTimer extends GameDefault {

    @Getter
    private final int minutes, seconds;
    @Getter
    private int currentTime, currentMinutes, currentSeconds;
    @Getter
    private String formattedTime;

    @Getter
    private GameTask gameTask;

    public GameTimer(int minutes, int seconds) {
        super(GameAPI.getAPI().generateGameDefaultId("GameTimer"));
        this.minutes = this.currentMinutes = minutes;
        this.seconds = this.currentSeconds = seconds;

        this.currentTime = this.minutes*60+this.seconds;

    }

    GameTimer(String id, int minutes, int seconds) {
        super(id);
        this.minutes = this.currentMinutes = minutes;
        this.seconds = this.currentSeconds = seconds;

        this.currentTime = this.minutes*60+this.seconds;

    }

    public void start() {
        if(this.gameTask == null) {
            this.gameTask = GameAPI.getAPI().runRepeatingSync(() -> {
                countAndFormat();
                Bukkit.getPluginManager().callEvent(new GameTimerTickEvent(this));
            }, 20L);
        }
    }

    private void end() {
        if(this.gameTask != null) {
            GameAPI.getAPI().cancelTask(this.gameTask);
            Bukkit.getPluginManager().callEvent(new GameTimerFinishedEvent(this));
        }
    }

    private void countAndFormat() {
        this.currentTime--;

        if (this.currentSeconds > 0) {
            this.currentSeconds--;
        } else {
            this.currentSeconds = 59;
            this.currentMinutes--;
        }

        if (this.currentTime < 60) {
            this.formattedTime = String.valueOf(this.currentSeconds + " Sekunde"+(this.currentSeconds == 1 ? "" : "n"));
        } else {
            if (this.currentSeconds < 10) this.formattedTime = this.currentMinutes + ":0" + this.currentSeconds;
            else this.formattedTime = this.currentMinutes + ":" + this.currentSeconds;
        }

        if(this.currentTime == 60) {
            end();
        }
    }

}
