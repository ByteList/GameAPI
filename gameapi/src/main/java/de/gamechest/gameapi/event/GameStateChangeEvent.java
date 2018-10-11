package de.gamechest.gameapi.event;

import de.gamechest.gameapi.GameState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 26.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameStateChangeEvent extends Event implements Cancellable {

    @Getter
    private static final HandlerList handlerList = new HandlerList();
    @Getter
    private final GameState old;
    @Getter
    private final GameState gameState;

    @Getter@Setter
    private boolean cancelled;

    public GameStateChangeEvent(GameState old, GameState gameState) {
        this.old = old;
        this.gameState = gameState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
