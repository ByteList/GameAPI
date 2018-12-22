package de.gamechest.gameapi;

import de.gamechest.gameapi.event.GameStateChangeEvent;
import de.gamechest.gameapi.listener.GameListener;
import de.gamechest.gameapi.listener.GameListenerManager;
import de.gamechest.gameapi.map.GameMap;
import de.gamechest.gameapi.scoreboard.GameScoreboard;
import de.gamechest.gameapi.task.GameTask;
import de.gamechest.gameapi.task.GameTaskManager;
import de.gamechest.gameapi.team.GameTeam;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameAPI {

    private static GameAPI gameAPI;
    private final GamePlugin plugin;
    @Getter
    private final File homeDirectory, mapsDirectory, rootDirectory, unloadedMapsDirectory;

    @Getter
    private String apiVersion;
    @Getter
    private String prefix;
    @Getter
    private String game;
    @Getter
    private GameState gameState;

    private GameTaskManager gameTaskManager;
    private GameListenerManager gameListenerManager;
    @Getter
    private HashMap<Player, GameScoreboard> gameScoreboards;
    @Getter
    private HashMap<Player, GameTeam> gameTeams;
    @Getter
    private ArrayList<GameMap> gameMaps;


    GameAPI(GamePlugin plugin, String game, String prefix) {
        gameAPI = this;
        this.plugin = plugin;

        this.rootDirectory = new File("./");
        this.homeDirectory = plugin.getDataFolder();
        this.mapsDirectory = new File(this.homeDirectory, "maps/");
        this.unloadedMapsDirectory = new File(this.homeDirectory, "unloadedMaps/");
        if(!this.mapsDirectory.exists()) {
            this.mapsDirectory.mkdirs();
        }
        if(!this.unloadedMapsDirectory.exists()) {
            this.unloadedMapsDirectory.mkdirs();
        }

        this.game = game;
        this.prefix = prefix;
        this.gameState = GameState.SETUP;

        this.gameTaskManager = new GameTaskManager(plugin);
        this.gameListenerManager = new GameListenerManager(plugin);

        this.gameScoreboards = new HashMap<>();
        this.gameTeams = new HashMap<>();
        this.gameMaps = new ArrayList<>();

        this.loadMaps();

        String[] v = this.getClass().getPackage().getImplementationVersion().split(":");
        apiVersion = v[0]+":"+v[1].substring(0, 7);
    }

    public static boolean isGame(JavaPlugin plugin) {
        return plugin instanceof GamePlugin;
    }

    public static GameAPI getAPI() {
        return gameAPI;
    }

    void enable() {

        plugin.getLogger().info("Enabled! Hooked into "+toString());
    }

    void disable() {
        plugin.getLogger().info("Disabled. ("+toString()+")");
    }

    @Override
    public String toString() {
        String[] ver = apiVersion.split(":");
        return "GameAPI{version="+ver[0]+", git="+ver[1]+", timer="+game+"}";
    }

    public void setGameState(GameState gameState) {
        if(gameState == GameState.LISTENER_EVERY_STATE) {
            throw new IllegalArgumentException(gameState.name() + " can only be used while register a listener");
        }
        GameStateChangeEvent event = new GameStateChangeEvent(this.gameState, gameState);
        Bukkit.getPluginManager().callEvent(event);

        if(!event.isCancelled()) {
            this.gameTaskManager.runStateTasks(gameState);
            this.gameListenerManager.registerListeners(this.gameState, gameState);
            this.gameState = gameState;
        }
    }

    public GameTask runStateTaskAsync(GameState gameState, Runnable runnable) {
        return this.gameTaskManager.runStateTaskAsync(gameState, runnable);
    }

    public GameTask runStateTask(GameState gameState, Runnable runnable) {
        return this.gameTaskManager.runStateTask(gameState, runnable);
    }

    public GameTask runTaskAsync(Runnable runnable) {
        return this.gameTaskManager.runTaskAsync(runnable);
    }

    public GameTask runTask(Runnable runnable) {
        return this.gameTaskManager.runTask(runnable);
    }

    public GameTask runSync(Runnable runnable, Long ticks) {
        return this.gameTaskManager.runSync(runnable, ticks);
    }

    public GameTask runRepeatingSync(Runnable runnable, Long ticks) {
        return this.gameTaskManager.runRepeatingSync(runnable, ticks);
    }

    public GameTask runStateRepeatingSync(GameState gameState, Runnable runnable, Long ticks) {
        return this.gameTaskManager.runStateRepeatingSync(gameState, runnable, ticks);
    }

    public void cancelTask(GameTask taskId) {
        this.gameTaskManager.cancelTask(taskId);
    }

    public void registerListener(GameState gameState, GameListener listener) {
        this.gameListenerManager.registerListener(gameState, listener);
    }

    public GameScoreboard getGameScoreboard(Player player) {
        return this.gameScoreboards.get(player);
    }

    public GameTeam getGameTeam(Player player) {
        return this.gameTeams.get(player);
    }

    public String generateGameDefaultId(String clazz) {
        return clazz+"-"+(System.currentTimeMillis()/1000);
    }

    public void sendJoinMessage(String displayname) {
        sendJoinMessage(displayname, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    public void sendJoinMessage(String displayname, List<Player> players) {
        players.forEach(player -> player.sendMessage("§8\u00BB "+displayname+" §7ist dem Spiel beigetreten"));
    }

    public void sendQuitMessage(String displayname) {
        sendQuitMessage(displayname, new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    public void sendQuitMessage(String displayname, List<Player> players) {
        players.forEach(player -> player.sendMessage("§8\u00BB "+displayname+" §7hat das Spiel verlassen"));
    }

    public void loadMaps() {
        this.gameMaps.clear();
        File[] files = this.mapsDirectory.listFiles((dir, name) -> dir == this.mapsDirectory);
        if(files == null) return;

        for (File map : files) {
            this.gameMaps.add(new GameMap(map));
        }
    }
}
