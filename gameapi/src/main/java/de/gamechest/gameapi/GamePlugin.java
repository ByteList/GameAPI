package de.gamechest.gameapi;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class GamePlugin extends JavaPlugin {

    protected GameAPI gameAPI;

    public GamePlugin(String game, String prefix) {
        gameAPI = new GameAPI(this, game, prefix);
    }

    public void onEnable() {
        gameAPI.enable();
    }

    public void onDisable() {
        gameAPI.disable();
    }
}
