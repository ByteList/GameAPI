package de.gamechest.gameapi.event;

import de.gamechest.gameapi.timer.GameTimer;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameTimerFinishedEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();
    @Getter
    private final GameTimer gameTimer;

    public GameTimerFinishedEvent(GameTimer gameTimer) {
        this.gameTimer = gameTimer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
