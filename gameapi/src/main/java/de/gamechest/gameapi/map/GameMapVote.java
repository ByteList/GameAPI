package de.gamechest.gameapi.map;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.gamechest.GameChest;
import de.gamechest.ItemBuilder;
import de.gamechest.gameapi.Callback;
import de.gamechest.gameapi.GameAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 30.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class GameMapVote {

    private HashMap<Integer, GameMap> mapsInPool = new HashMap<>();
    private HashMap<Integer, Integer> votesPerMap = new HashMap<>();
    private HashMap<Player, Integer> votedMap = new HashMap<>();
    private List<Player> openedInventory = new ArrayList<>();

    private final String inventoryName = "§1Voting";

    @Getter@Setter
    private boolean votingEnabled;
    @Getter
    private GameMap winner;

    public GameMapVote() {
        if (GameChest.getInstance().isCloudEnabled())
            ByteCloudCore.getInstance().getCloudAPI().setMotd("Votingphase...");
        List<GameMap> arenas = new ArrayList<>(GameAPI.getAPI().getGameMaps());

        int n = 3;

        while (n != 0) {
            n--;

            int max = arenas.size();
            int rnd = ThreadLocalRandom.current().nextInt(max);
            GameMap voteMap = arenas.get(rnd);

            arenas.remove(voteMap);
            mapsInPool.put(n, voteMap);
            votesPerMap.put(n, 0);
        }
        this.votingEnabled = true;
    }

    public void setItem(Player player, int slot) {
        ItemStack i = new ItemStack(Material.EMPTY_MAP);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName("§bMapvoting");
        m.setLore(Collections.singletonList("§7§oHier kannst du für eine Karte abstimmen!"));
        i.setItemMeta(m);

        player.getInventory().setItem(slot, i);
    }

    private void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, this.inventoryName);
        ItemStack itemStack = ItemBuilder.getPlaceholder();
        int size = this.mapsInPool.size();

        for (int i = 0; i < 27; i++) inventory.setItem(i, itemStack);

        switch (size) {
            case 1:
                inventory.setItem(13, generate(this.mapsInPool.get(0)));
                break;
            case 2:
                inventory.setItem(11, generate(this.mapsInPool.get(0)));
                inventory.setItem(15, generate(this.mapsInPool.get(1)));
                break;
            case 3:
                inventory.setItem(10, generate(this.mapsInPool.get(0)));
                inventory.setItem(13, generate(this.mapsInPool.get(1)));
                inventory.setItem(16, generate(this.mapsInPool.get(2)));
                break;
        }

        openedInventory.add(player);
        player.openInventory(inventory);
    }

    private ItemStack generate(GameMap gameMap) {
        YamlConfiguration config = gameMap.getConfiguration();
        return ItemBuilder.newBuilder(
                Material.getMaterial(config.getString("item.material")),
                Byte.parseByte(config.getString("item.data")))
                .displayname("§e"+gameMap.getDisplayname())
                .lore(null, "§7§oVotes: §e" + this.votesPerMap.get(0))
                .get();
    }

    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                && e.getItem().getItemMeta().getDisplayName().equals("§bMapvoting")) {
            e.setCancelled(true);
            openInventory(p);
        }
    }

    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().equalsIgnoreCase(this.inventoryName)) {
            openedInventory.remove(e.getPlayer());
        }
    }

    public void onInventoryClick(InventoryClickEvent e, Runnable runAtSuccess) {
        Player player = (Player) e.getWhoClicked();

        if(e.getInventory().getName().equals(this.inventoryName)) {
            e.setCancelled(true);
            if(e.getCurrentItem() != null && e.getCurrentItem().getItemMeta().getDisplayName().startsWith("§e")) {
                this.mapsInPool.forEach((integer, gameMap) -> {
                    if(e.getCurrentItem().getItemMeta().getDisplayName().equals("§e"+gameMap.getDisplayname())) {
                        if (votedMap.containsKey(player)) {
                            votesPerMap.put(votedMap.get(player), votesPerMap.get(votedMap.get(player)) - 1);
                            player.sendMessage("§8\u00BB §6Du hast deine Stimme auf die Map §a" + gameMap.getDisplayname() + " §6geändert.");
                        } else {
                            player.sendMessage("§8\u00BB §6Du hast für die Map §a" + gameMap.getDisplayname() + " §6gestimmt.");
                        }
                        votedMap.put(player, integer);
                        votesPerMap.put(integer, votesPerMap.get(integer) + 1);

                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 3F);
                        runAtSuccess.run();
                    }
                });
            }
        }
    }

    public void end(List<Player> players, int slot, Callback<Player> runAtSuccess) {
        int max = 0;
        GameMap gameMap = null;

        for (int i : votesPerMap.values()) if (i > max) max = i;
        for (int all : votesPerMap.keySet()) if (votesPerMap.get(all) == max) gameMap = this.mapsInPool.get(all);

        if(gameMap == null)
            return;

        this.winner = gameMap;

        if (GameChest.getInstance().isCloudEnabled())
            ByteCloudCore.getInstance().getCloudAPI().setMotd(gameMap.getDisplayname());

        String map = gameMap.getDisplayname();

        players.forEach(player -> {
            player.getInventory().clear(slot);
            if (openedInventory.contains(player))
                player.closeInventory();

            player.sendMessage("");
            player.sendMessage(GameAPI.getAPI().getPrefix() + "§eVoting beendet!");
            player.sendMessage(GameAPI.getAPI().getPrefix() + "§fGespielt wird auf: §a" + map);
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 2F, 3F);
            runAtSuccess.run(player);
        });
    }

    public void forceMap(int gameMap) {
        for (int map : votesPerMap.keySet()) {
            votesPerMap.put(map, 0);
        }
        votesPerMap.put(gameMap, 500);
    }

    public boolean hasVoted(Player player) {
        return votedMap.containsKey(player);
    }
}
