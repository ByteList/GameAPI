package de.gamechest.gameapi.test.listener;

import de.gamechest.gameapi.event.GameStateChangeEvent;
import de.gamechest.gameapi.listener.GameListener;
import de.gamechest.gameapi.test.TestGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

/**
 * Created by ByteList on 26.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameStateChangeListener implements GameListener {

    private int i = 0;

    @EventHandler
    public void onStateChange(GameStateChangeEvent e) {
        TestGame.getInstance().getTestScoreboard().changeInteractiveLine("§7 / ", "§e"+(++i), "§c12");
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();

        if(e.isSneaking()) {
            TestGame.getInstance().getTest2Scoreboard().send(player);
        } else {
            TestGame.getInstance().getTestScoreboard().send(player);
        }
    }
}
