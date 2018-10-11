package de.gamechest.gameapi;

import lombok.Getter;

/**
 * Created by ByteList on 21.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class GameDefault {

    @Getter
    protected final String id;

    public GameDefault(String id) {
        this.id = id;
    }
}
