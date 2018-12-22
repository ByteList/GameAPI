package de.gamechest.gameapi.timer;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.event.GameTimerTickEvent;
import de.gamechest.gameapi.task.GameTask;
import lombok.Getter;
import org.bukkit.Bukkit;

/**
 * Created by ByteList on 22.12.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameRepeatingTimer extends GameTimer {

    @Getter
    private long repeatingTicks;

    @Getter
    private GameTask gameTask;

    public GameRepeatingTimer(long ticks) {
        super(GameAPI.getAPI().generateGameDefaultId("GameRepeatingTimer"), 0);
        this.repeatingTicks = ticks;
    }

    @Override
    public void start() {
        if(this.gameTask == null) {
            this.gameTask = GameAPI.getAPI().runRepeatingSync(() -> {
                Bukkit.getPluginManager().callEvent(new GameTimerTickEvent(this));
            }, this.repeatingTicks);
        }
    }

    @Override
    public boolean isRepeatingTimer() {
        return true;
    }

    @Override
    public String getFormattedTime() {
        return "error:isRep";
    }
}
