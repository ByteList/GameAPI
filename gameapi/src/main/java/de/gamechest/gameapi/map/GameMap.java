package de.gamechest.gameapi.map;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameDefault;
import lombok.Getter;

import java.io.File;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMap extends GameDefault {

    @Getter
    private final File directory, config, mapFolder;

    public GameMap(String id) {
        super(id);
        this.directory = new File(GameAPI.getAPI().getMapsDirectory(), id);
        this.config = new File(this.directory, "config.yml");
        this.mapFolder = new File(this.directory, "world/");
    }
}
