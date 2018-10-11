package de.gamechest.gameapi.test.listener;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameState;
import de.gamechest.gameapi.listener.GameListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TestListener implements GameListener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        GameAPI.getAPI().runTask(()-> event.getPlayer().sendMessage("Task sync!"));
        GameAPI.getAPI().runTaskAsync(()-> event.getPlayer().sendMessage("Task async!"));

        GameAPI.getAPI().runStateTask(GameState.INGAME, ()-> event.getPlayer().sendMessage("INGAME: Task sync!"));
        GameAPI.getAPI().runStateTaskAsync(GameState.INGAME, ()-> event.getPlayer().sendMessage("INGAME: Task async!"));
    }
}
