package de.gamechest.gameapi.countdown;

import de.gamechest.gameapi.GameAPI;
import de.gamechest.gameapi.GameState;

/**
 * Created by ByteList on 22.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameStartCountdown extends GameCountdown {

    public GameStartCountdown() {
        super(GameState.LOBBY, 61, GameCountdownNotifyType.CHAT,
                new int[]{60, 40, 30, 20, 10, 5, 4, 3, 2, 1},
                GameAPI.getAPI().getPrefix()+"§7Das Spiel beginnt in §e%seconds%§7 Sekunde%n%",
                "§4%seconds%", "§cSekunde%n% bis zum Start!");

        this.runBeforeNotify(players-> {
            switch (this.getCurrentTime()) {
                case 5:
                    this.setNotifyType(GameCountdownNotifyType.TITLE);
                    break;
                case 4:
                    this.setTitle("§6%seconds%");
                    break;
                case 3:
                    this.setTitle("§e%seconds%");
                    break;
                case 2:
                    this.setTitle("§a%seconds%");
                    break;
                case 1:
                    this.setTitle("§2%seconds%");
                    break;
            }

        });
    }
}
