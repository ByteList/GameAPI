package de.gamechest.gameapi.countdown;

import de.gamechest.BountifulAPI;
import de.gamechest.gameapi.Callback;
import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import de.gamechest.gameapi.GameState;
import de.gamechest.gameapi.task.GameTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ByteList on 22.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameCountdown extends GameDefault {

    private GameState gameState;
    private GameTask gameTask;
    @Getter
    private int startTime, currentTime;
    @Getter @Setter
    private String message, title, subTitle;

    private Runnable runnable;
    private Callback<Collection<Player>> runAtEnd, runBeforeNotify, runAtEveryTick;
    @Getter @Setter
    private GameCountdownNotifyType notifyType;

    private ArrayList<Player> players = new ArrayList<>();

    private int[] notifyTimes;

    public GameCountdown(GameState gameState, int startTime, GameCountdownNotifyType notifyType, int[] notifyTimes, String message) {
        this(gameState, startTime, notifyType, notifyTimes, message, "", "");
    }

    public GameCountdown(GameState gameState, int startTime, GameCountdownNotifyType notifyType, int[] notifyTimes, String message, String title, String subTitle) {
        super(GameAPI.getAPI().generateGameDefaultId("GameCountdown"));
        this.gameState = gameState;
        this.currentTime = startTime;
        this.startTime = startTime;
        this.notifyType = notifyType;
        this.notifyTimes = notifyTimes;
        this.message = message;
        this.title = title;
        this.subTitle = subTitle;

        this.runnable = ()-> {
            if(this.currentTime > 0) {
                this.currentTime--;
                this.runAtEveryTick.run(this.getPlayers());

                for (int i : this.notifyTimes) {
                    if(i == this.currentTime) sendNotify();
                }
            } else {
                end();
            }
        };
    }

    public boolean startIf(boolean b) {
        if(b) start();

        return b;
    }

    public void start() {
        if(this.gameTask == null) this.gameTask = GameAPI.getAPI().runStateRepeatingSync(gameState, this.runnable, 20L);
    }

    public void pause() {
        if(this.gameTask != null) {
            GameAPI.getAPI().cancelTask(this.gameTask);
            this.gameTask = null;
        }
    }

    public void end() {
        pause();
        this.currentTime = 0;
        this.runAtEnd.run(this.getPlayers());
    }

    public void restart() {
        end();
        this.currentTime = this.startTime;
        start();
    }

    public boolean isRunning() {
        return this.gameTask != null;
    }

    private void sendNotify() {
        this.runBeforeNotify.run(this.getPlayers());

        // §7Das Spiel beginnt in §e%seconds%§7 Sekunde%n%
        String message = replace(this.message), title = replace(this.title), subTitle = replace(this.subTitle);

        switch (this.notifyType) {
            case CHAT:
                this.getPlayers().forEach(player -> player.sendMessage(message));
                break;
            case ACTION_BAR:
                this.getPlayers().forEach(player -> BountifulAPI.sendActionBar(player, message));
                break;
            case TITLE:
                this.getPlayers().forEach(player -> BountifulAPI.sendTitle(player, 5, 20, 5, title, subTitle));
                break;
        }

        this.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.RECORD_BLOCKS, 2F, 2F));
    }


    public void runAtEnd(Callback<Collection<Player>> runnable) {
        this.runAtEnd = runnable;
    }

    public void runBeforeNotify(Callback<Collection<Player>> runnable) {
        this.runBeforeNotify = runnable;
    }

    public void runAtEveryTick(Callback<Collection<Player>> runnable) {
        this.runAtEveryTick = runnable;
    }

    public void setCurrentTime(int time) {
        this.currentTime = time;
    }

    private String replace(String string) {
        return string.replace("%seconds%", String.valueOf(this.currentTime)).replace("%n%", (this.currentTime != 1 ? "n" : ""));
    }

    public void addPlayer(Player player) {
        if(!this.players.contains(player))
            this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Collection<Player> getPlayers() {
        return Collections.unmodifiableCollection(this.players);
    }

    public void addPlayers(List<Player> players) {
        players.forEach(this::addPlayer);
    }

    public void removePlayers(List<Player> players) {
        players.forEach(this::removePlayer);
    }

    public void addPlayers(Player... players) {
        this.addPlayers(players);
    }

    public void removePlayers(Player... players) {
        this.removePlayers(players);
    }
}
