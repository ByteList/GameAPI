package de.gamechest.gameapi.test;

import de.gamechest.TabList;
import de.gamechest.gameapi.GamePlugin;
import de.gamechest.gameapi.GameState;
import de.gamechest.gameapi.countdown.GameCountdown;
import de.gamechest.gameapi.countdown.GameCountdownNotifyType;
import de.gamechest.gameapi.countdown.GameStartCountdown;
import de.gamechest.gameapi.scoreboard.GameScoreboard;
import de.gamechest.gameapi.team.GameTeam;
import de.gamechest.gameapi.test.listener.*;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 28.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TestGame extends GamePlugin {

    @Getter
    private static TestGame instance;
    @Getter
    private GameScoreboard testScoreboard, test2Scoreboard;
    @Getter
    private GameTeam testTeam, test2Team;
    @Getter
    private GameCountdown testCountdown, test2Countdown;

    public TestGame() {
        super("TestGame", "§cTestGame §8\u00BB ");
    }

    @Override
    public void onEnable() {
        instance = this;

        gameAPI.registerListener(GameState.LISTENER_EVERY_STATE, new JoinListener());
        gameAPI.registerListener(GameState.LISTENER_EVERY_STATE, new GameStateChangeListener());
        gameAPI.registerListener(GameState.LISTENER_EVERY_STATE, new TestListener());
        gameAPI.registerListener(GameState.LOBBY, new LobbyChatListener());
        gameAPI.registerListener(GameState.INGAME, new IngameChatListener());

        testScoreboard = new GameScoreboard("test")
                .setHeader("§2§lTestGame")
                .addLine("Ja") // 2
                .addInteractiveLine("§7 / ", "§e0", "§c12")
                .addLine("Bloo") // 0
                .build();

        test2Scoreboard = new GameScoreboard("test23")
                .setHeader("§e§lTestGame")
                .addLine("Ja") // 2
                .addInteractiveLine("§3Value: ", "", "§c0")
                .build();

        testTeam = new GameTeam("testTeam", TabList.TabListMode.DARK_PURPLE);
        test2Team = new GameTeam("test2Team", "01", "§3PREFIX! > ", " §7[§8lol§7]");

        testCountdown = new GameStartCountdown();

        test2Countdown = new GameCountdown(GameState.INGAME, 31, GameCountdownNotifyType.CHAT,
                new int[]{30, 20, 10, 5, 4, 3, 2, 1}, "§7Das Spiel beginnt in §e%seconds%§7 Sekunde%n%");

        getCommand("lobby").setExecutor((commandSender, command, s, strings) -> {
            gameAPI.setGameState(GameState.LOBBY);
            this.test2Scoreboard.changeInteractiveLine((Player) commandSender, "§3Value: ", null, "§a1");
            TestGame.getInstance().getTestTeam().addMember((Player) commandSender);
            return true;
        });
        getCommand("ingame").setExecutor((commandSender, command, s, strings) -> {
            gameAPI.setGameState(GameState.INGAME);
            this.test2Scoreboard.changeInteractiveLine((Player) commandSender, "§3Value: ", null, "§e2");
            TestGame.getInstance().getTest2Team().addMember((Player) commandSender);
            return true;
        });
        getCommand("end").setExecutor((commandSender, command, s, strings) -> {
            gameAPI.setGameState(GameState.END);
            return true;
        });

        getCommand("test").setExecutor((commandSender, command, s, strings) -> {

            return true;
        });

        super.onEnable();
    }
}