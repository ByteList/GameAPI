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
    private int currentTime;
    @Getter
    private String formattedTime;

    @Getter
    private GameTask gameTask;

    public GameTimer(int seconds) {
        this(GameAPI.getAPI().generateGameDefaultId("GameTimer"), seconds);
    }

    public GameTimer(String id, int seconds) {
        super(id);
        this.currentTime = seconds;
    }

    public void start() {
        if(this.gameTask == null) {
            this.gameTask = GameAPI.getAPI().runRepeatingSync(() -> {
                countAndFormat();
                Bukkit.getPluginManager().callEvent(new GameTimerTickEvent(this));
            }, 20L);
        }
    }

    public void end() {
        if(this.gameTask != null) {
            GameAPI.getAPI().cancelTask(this.gameTask);
            this.gameTask = null;
            Bukkit.getPluginManager().callEvent(new GameTimerFinishedEvent(this));
        }
    }

    public boolean isRunning() {
        return this.gameTask != null;
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private void countAndFormat() {
        this.currentTime--;

        if (this.currentTime < 60) {
            this.formattedTime = String.valueOf(this.currentTime + " Sekunde"+(this.currentTime == 1 ? "" : "n"));
        } else {
            int minutes = (this.currentTime % 3600) / 60;
            int seconds = this.currentTime % 60;
            this.formattedTime = twoDigitString(minutes) + ":" + twoDigitString(seconds);
        }

        if(this.currentTime == 0) {
            end();
        }
    }

    public boolean isRepeatingTimer() {
        return false;
    }
}
