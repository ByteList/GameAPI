package de.gamechest.gameapi.scoreboard;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * Created by ByteList on 21.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameScoreboardLine {

    private final GameScoreboard gameScoreboard;

    private String prefix;
    private String staticValue;
    private String suffix;

    @Getter
    private boolean interactive;


    GameScoreboardLine(GameScoreboard gameScoreboard, String line) {
        this(gameScoreboard, false);

        this.staticValue = line;
    }

    GameScoreboardLine(GameScoreboard gameScoreboard, String prefix, String staticValue, String suffix) {
        this(gameScoreboard, true);

        this.prefix = prefix;
        this.staticValue = staticValue;
        this.suffix = suffix;
    }

    private GameScoreboardLine(GameScoreboard gameScoreboard, boolean interactive) {
        this.gameScoreboard = gameScoreboard;
        this.interactive = interactive;
    }


    public void setPrefix(String prefix) {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        set(prefix, null);
    }

    public String getPrefix() {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        Validate.notNull(this.prefix, "Prefix is null");

        return this.prefix;
    }

    public String getStaticValue() {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        Validate.notNull(this.prefix, "StaticValue is null");

        return this.staticValue;
    }

    public void setSuffix(String suffix) {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        set(null, suffix);
    }

    public String getSuffix() {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        Validate.notNull(this.prefix, "Suffix is null");

        return this.suffix;
    }

    public void set(String prefix, String suffix) {
        Validate.isTrue(this.interactive, "GameScoreboardLine is not interactive");
        if(prefix == null) prefix = this.prefix;
        if(suffix == null) suffix = this.suffix;

        String finalPrefix = prefix;
        String finalSuffix = suffix;
        this.gameScoreboard.getUpdateOnly().forEach(player -> {
            Team team = player.getScoreboard().getTeam(this.staticValue) != null ? player.getScoreboard().getTeam(this.staticValue) : registerTeam(player.getScoreboard());
            team.setPrefix(finalPrefix);
            team.setSuffix(finalSuffix);
        });
        this.gameScoreboard.updateOnly(this.gameScoreboard.getPlayers());
    }

    public String getLine() {
        Validate.notNull(this.staticValue, "StaticValue is null");

        return this.staticValue;
    }

    public Team registerTeam(Scoreboard scoreboard) {
        if(scoreboard.getTeam(this.staticValue) == null) {
            Team team = scoreboard.registerNewTeam(this.staticValue);
            team.addEntry(this.staticValue);
            if(this.prefix != null) team.setPrefix(this.prefix);
            if(this.suffix != null) team.setSuffix(this.suffix);

            return team;
        }

        return scoreboard.getTeam(this.staticValue);
    }
}
