package de.gamechest.gameapi.test.listener;

import de.gamechest.gameapi.listener.GameListener;
import de.gamechest.gameapi.test.TestGame;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by ByteList on 26.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class JoinListener implements GameListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        TestGame.getInstance().getTestScoreboard().send(player);
        TestGame.getInstance().getTestTeam().addMember(player);

        if(!TestGame.getInstance().getTestCountdown().startIf(Bukkit.getOnlinePlayers().size() == 2)) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage());
        }
    }
}
