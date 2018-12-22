package de.gamechest.gameapi.map;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMap extends GameDefault {

    @Getter
    private final File directory, configFile, mapFolder, worldFolder;
    @Getter
    private final YamlConfiguration configuration;
    @Getter
    private World world;

    @Getter @Setter
    private boolean inVoting;

    public GameMap(File directory) {
        super(directory.getName());
        this.directory = directory;
        this.configFile = new File(this.directory, "config.yml");
        this.mapFolder = new File(this.directory, "world/");

        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);

        if(!this.configFile.exists()) {
            this.configuration.set("displayname", directory.getName());
            this.configuration.set("game", GameAPI.getAPI().getGame());
            this.configuration.set("item.material", Material.STONE.name());
            this.configuration.set("item.data", "0");

            try {
                this.configuration.save(this.configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.worldFolder = new File(GameAPI.getAPI().getRootDirectory(), this.configuration.getString("displayname"));
    }

    public void loadMap() {
        if(this.worldFolder.exists()) return;
        String name = this.configuration.getString("displayname");

        try {
            FileUtils.copyDirectory(this.mapFolder,
                    new File(GameAPI.getAPI().getRootDirectory(), name));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.world = Bukkit.createWorld(new WorldCreator(name));
    }

    public void unloadMap() {
        if(Bukkit.unloadWorld(this.configuration.getString("displayname"), false)) {
            this.world = null;
            try {
                FileUtils.moveDirectory(this.worldFolder, GameAPI.getAPI().getUnloadedMapsDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDisplayname() {
        return this.configuration.getString("displayname");
    }
}
