package de.gamechest.gameapi.test.listener;

import de.gamechest.gameapi.listener.GameListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class LobbyChatListener implements GameListener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage("§a"+e.getMessage());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // ...
    }
}
