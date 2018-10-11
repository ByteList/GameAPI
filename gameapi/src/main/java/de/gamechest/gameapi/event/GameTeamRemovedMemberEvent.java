package de.gamechest.gameapi.event;

import de.gamechest.gameapi.team.GameTeam;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 22.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameTeamRemovedMemberEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private final Player player;
    @Getter
    private final GameTeam gameTeam;

    public GameTeamRemovedMemberEvent(Player player, GameTeam gameTeam) {
        this.player = player;
        this.gameTeam = gameTeam;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
