package de.gamechest.gameapi.team;

import de.gamechest.TabList;
import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import de.gamechest.gameapi.event.GameTeamAddedMemberEvent;
import de.gamechest.gameapi.event.GameTeamRemovedMemberEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ByteList on 22.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameTeam extends GameDefault {

    private ArrayList<Player> member = new ArrayList<>();
    @Getter @Setter
    private boolean friendlyFireEnabled, showPrefixAndSuffix;

    private final String pos;
    private final TabList.TabListMode tabListMode;

    @Getter @Setter
    private String prefix, suffix;

    public GameTeam(String id, String pos, String prefix, String suffix) {
        super(id);
        this.pos = pos;
        this.tabListMode = TabList.TabListMode.CUSTOM;
        this.prefix = prefix;
        this.suffix = suffix;
        this.showPrefixAndSuffix = true;
    }

    public GameTeam(String id, TabList.TabListMode tabListMode) {
        super(id);
        this.pos = "";
        this.tabListMode = tabListMode;
        if(tabListMode.isColor()) {
            this.prefix = "§"+tabListMode.getColorCode();
        }
        this.suffix = "";
        this.showPrefixAndSuffix = true;
    }

    public GameTeam addMember(Player player) {
        if(!this.member.contains(player)) {
            this.member.add(player);
            GameTeam old = GameAPI.getAPI().getGameTeam(player);
            if(old != null) {
                old.removeMember(player);
            }
            GameAPI.getAPI().getGameTeams().put(player, this);

            if(this.showPrefixAndSuffix) {
                if(this.tabListMode == TabList.TabListMode.CUSTOM) TabList.updateCustom(player, pos, prefix, suffix);
                else TabList.update(player, this.tabListMode);
            }

            Bukkit.getPluginManager().callEvent(new GameTeamAddedMemberEvent(player, this));
        }

        return this;
    }

    public GameTeam removeMember(Player player) {
        if(this.member.contains(player)) {
            this.member.remove(player);

            TabList.update(player, TabList.TabListMode.WHITE);

            Bukkit.getPluginManager().callEvent(new GameTeamRemovedMemberEvent(player, this));
        }

        return this;
    }

    public boolean isMember(Player player) {
        return this.member.contains(player);
    }

    public ArrayList<Player> getMember() {
        return new ArrayList<>(this.member);
    }

    public GameTeam updateTabList() {
        this.getMember().forEach(this::updateTabList);
        return this;
    }

    public GameTeam updateTabList(Player player) {
        if(this.showPrefixAndSuffix && isMember(player)) {
            if(this.tabListMode == TabList.TabListMode.CUSTOM) TabList.updateCustom(player, pos, prefix, suffix);
            else TabList.update(player, this.tabListMode);
        } else TabList.update(player, TabList.TabListMode.WHITE);
        return this;
    }
}
