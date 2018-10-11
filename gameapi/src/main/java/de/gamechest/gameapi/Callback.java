package de.gamechest.gameapi;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface Callback<T> {

    void run(T result);
}
