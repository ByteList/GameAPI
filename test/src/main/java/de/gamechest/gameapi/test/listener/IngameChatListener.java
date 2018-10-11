package de.gamechest.gameapi.test.listener;

import de.gamechest.gameapi.listener.GameListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class IngameChatListener implements GameListener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setMessage("§e"+e.getMessage());
    }
}
