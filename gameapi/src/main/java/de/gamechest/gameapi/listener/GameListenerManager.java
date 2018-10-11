package de.gamechest.gameapi.listener;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GamePlugin;
import de.gamechest.gameapi.GameState;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameListenerManager {

    private final GamePlugin gamePlugin;

    private final HashMap<GameState, List<GameListener>> listeners = new HashMap<>();

    public GameListenerManager(GamePlugin gamePlugin) {
        this.gamePlugin = gamePlugin;
        for(GameState gameState : GameState.values()) listeners.put(gameState, new ArrayList<>());
    }

    public void registerListener(GameState gameState, GameListener gameListener) {
        List<GameListener> list = listeners.get(gameState);
        if(!list.contains(gameListener)) list.add(gameListener);
        if(GameAPI.getAPI().getGameState() == gameState ||gameState == GameState.LISTENER_EVERY_STATE) {
            Bukkit.getPluginManager().registerEvents(gameListener, gamePlugin);
        }
    }

    public void registerListeners(GameState old, GameState gameState) {
        List<GameListener> list = listeners.get(old);
        for(GameListener gameListener : list) {
            HandlerList.unregisterAll(gameListener);
        }
        list = listeners.get(gameState);
        for(GameListener gameListener : list) {
            Bukkit.getPluginManager().registerEvents(gameListener, gamePlugin);
        }

    }
}
