package de.gamechest.gameapi.scoreboard;

import de.gamechest.gameapi.Callback;
import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ByteList on 26.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameScoreboard extends GameDefault {

    @Getter
    private String header;
    @Getter
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<Integer, GameScoreboardLine> lines = new HashMap<>();
    private ArrayList<GameScoreboardLine> addedLines = new ArrayList<>();

    private HashMap<String, Integer> interactiveLines = new HashMap<>();

    public GameScoreboard(String id) {
        super(id);
    }

    public GameScoreboard setHeader(String header) {
        this.header = header;
        return this;
    }

    public GameScoreboard setLine(int number, String line) {
        this.lines.put(number, new GameScoreboardLine(this, line));
        return this;
    }

    public GameScoreboard addLine(String line) {
        this.addedLines.add(new GameScoreboardLine(this, line));
        return this;
    }

    public GameScoreboard setInteractiveLine(int number, String staticValue, String prefix, String suffix) {
        this.lines.put(number, new GameScoreboardLine(this, prefix, staticValue, suffix));
        return this;
    }

    public GameScoreboard addInteractiveLine(String staticValue, String prefix, String suffix) {
        this.addedLines.add(new GameScoreboardLine(this, prefix, staticValue, suffix));
        return this;
    }

    public GameScoreboard changeInteractiveLine(String staticValue, String prefix, String suffix) {
        GameScoreboardLine team = getLine(staticValue);
        team.set(this.players, prefix, suffix);
        return this;
    }

    public GameScoreboard changeInteractiveLine(Player player, String staticValue, String prefix, String suffix) {
        GameScoreboardLine team = getLine(staticValue);
        team.set(Collections.singletonList(player), prefix, suffix);
        return this;
    }

    public GameScoreboard removeLine(int number, Callback<Integer> onSuccess, Callback<Integer> onFailure) {
        if(this.lines.containsKey(number)) {
            this.lines.remove(number);
            onSuccess.run(number);
        } else {
            onFailure.run(number);
        }
        return this;
    }

    public GameScoreboard build() {
        Collections.reverse(this.addedLines);
        AtomicInteger i = new AtomicInteger(this.lines.size());
        this.addedLines.forEach(line -> {
            i.getAndIncrement();
            int number = i.get();
            this.lines.put(number, line);
            if(line.isInteractive()) this.interactiveLines.put(line.getStaticValue(), number);
        });

        return this;
    }

    public void send(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(this.id) != null ? scoreboard.getObjective(this.id) : scoreboard.registerNewObjective(this.id, "dummy");

        objective.setDisplayName(this.header);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        this.lines.forEach((number, line) -> {
            if(line.isInteractive()) line.registerTeam(scoreboard);

            objective.getScore(line.getLine()).setScore(number);
        });

        if(!this.players.contains(player)) {
            GameScoreboard old = GameAPI.getAPI().getGameScoreboard(player);
            if(old != null) {
                old.getPlayers().remove(player);
            }

            this.players.add(player);
            GameAPI.getAPI().getGameScoreboards().put(player, this);

            player.setScoreboard(scoreboard);
        }
    }

    public void send(Player... players) {
        for (Player player : players) {
            send(player);
        }
    }


    private GameScoreboardLine getLine(String staticValue) {
        Validate.isTrue(this.interactiveLines.containsKey(staticValue), staticValue+" isn't a interactive line!");

        return this.lines.get(this.interactiveLines.get(staticValue));
    }
}
