package de.gamechest.gameapi;

import lombok.Getter;

import java.util.HashMap;

/**
 * Created by ByteList on 21.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class GameDefault {

    @Getter
    protected final String id;

    @Getter
    private final HashMap<String, String> meta = new HashMap<>();

    public GameDefault(String id) {
        this.id = id;
    }
}
