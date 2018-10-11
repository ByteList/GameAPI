package de.gamechest.gameapi.task;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GamePlugin;
import de.gamechest.gameapi.GameState;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameTaskManager {

    private final GamePlugin gamePlugin;

    private final HashMap<GameState, List<Runnable>> queuedStateTasks = new HashMap<>();
    private final HashMap<GameState, List<Runnable>> queuedStateRepeatingTasks = new HashMap<>();
    private final HashMap<GameState, List<Runnable>> asyncQueuedStateTasks = new HashMap<>();

    public GameTaskManager(GamePlugin gamePlugin) {
        this.gamePlugin = gamePlugin;

        for(GameState gameState : GameState.values()) {
            queuedStateTasks.put(gameState, new ArrayList<>());
            queuedStateRepeatingTasks.put(gameState, new ArrayList<>());
            asyncQueuedStateTasks.put(gameState, new ArrayList<>());
        }
    }

    public void runStateTasks(GameState gameState) {
        if(queuedStateTasks.containsKey(gameState)) {
            List<Runnable> tasks = new ArrayList<>();
            tasks.addAll(queuedStateTasks.get(gameState));
            for(Runnable r : tasks) {
                Bukkit.getScheduler().runTask(gamePlugin, r);
            }
            queuedStateTasks.put(gameState, new ArrayList<>());
        }

        if(asyncQueuedStateTasks.containsKey(gameState)) {
            List<Runnable> tasks = new ArrayList<>();
            tasks.addAll(asyncQueuedStateTasks.get(gameState));
            for(Runnable r : tasks) {
                Bukkit.getScheduler().runTaskAsynchronously(gamePlugin, r);
            }
            asyncQueuedStateTasks.put(gameState, new ArrayList<>());
        }
    }

    public GameTask runStateTaskAsync(GameState gameState, Runnable runnable) {
        if (gamePlugin.isEnabled() && GameAPI.getAPI().getGameState() == gameState) {
            return new GameTask(Bukkit.getScheduler().runTaskAsynchronously(gamePlugin, runnable).getTaskId());
        } else {
            List<Runnable> tasks = asyncQueuedStateTasks.get(gameState);
            tasks.add(runnable);
            asyncQueuedStateTasks.put(gameState, tasks);
            return new GameTask(0);
        }
    }

    public GameTask runStateTask(GameState gameState, Runnable runnable) {
        if (gamePlugin.isEnabled() && GameAPI.getAPI().getGameState() == gameState) {
            return new GameTask(Bukkit.getScheduler().runTask(gamePlugin, runnable).getTaskId());
        } else {
            List<Runnable> tasks = queuedStateTasks.get(gameState);
            tasks.add(runnable);
            queuedStateTasks.put(gameState, tasks);
            return new GameTask(0);
        }
    }

    public GameTask runStateRepeatingSync(GameState gameState, Runnable runnable, long ticks) {
        if (gamePlugin.isEnabled() && GameAPI.getAPI().getGameState() == gameState) {
            return new GameTask(Bukkit.getScheduler().runTaskTimer(gamePlugin, runnable, 0, ticks).getTaskId());
        } else {
            List<Runnable> tasks = queuedStateRepeatingTasks.get(gameState);
            tasks.add(runnable);
            queuedStateRepeatingTasks.put(gameState, tasks);
            return new GameTask(0);
        }
    }

    public GameTask runTaskAsync(Runnable runnable) {
        return new GameTask(Bukkit.getScheduler().runTaskAsynchronously(gamePlugin, runnable).getTaskId());
    }

    public GameTask runTask(Runnable runnable) {
        return new GameTask(Bukkit.getScheduler().runTask(gamePlugin, runnable).getTaskId());
    }

    public GameTask runSync(Runnable runnable, Long ticks) {
        return new GameTask(Bukkit.getScheduler().runTaskLater(gamePlugin, runnable, ticks).getTaskId());
    }

    public GameTask runRepeatingSync(Runnable runnable, Long ticks) {
        return new GameTask(Bukkit.getScheduler().runTaskTimer(gamePlugin, runnable, 0, ticks).getTaskId());
    }

    public void cancelTask(GameTask taskId) {
        if (taskId == null) return;
        Bukkit.getScheduler().cancelTask(taskId.getId());
    }
}
