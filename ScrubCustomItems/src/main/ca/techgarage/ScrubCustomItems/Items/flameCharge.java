package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.World;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class flameCharge implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Integer> playerPlaytime = new HashMap<>();
    private final HashMap<UUID, Integer> pendingFlameCharges = new HashMap<>(); // Track pending charges

    public flameCharge(JavaPlugin plugin) {
        this.plugin = plugin;

        // Schedule a task to track playtime every second
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID playerId = player.getUniqueId();
                    playerPlaytime.put(playerId, playerPlaytime.getOrDefault(playerId, 0) + 1);

                    // Check if the player has played for 3600 seconds (1 hour)
                    if (playerPlaytime.get(playerId) >= 3600) {
                        if (isInValidWorld(player)) {
                            rewardFlameCharges(player);
                            playerPlaytime.put(playerId, 0); // Reset playtime after rewarding
                        } else {
                            // Store pending flame charges to give when they return to a valid world
                            pendingFlameCharges.put(playerId, pendingFlameCharges.getOrDefault(playerId, 0) + 1);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Runs every second (20 ticks)
    }

    // Helper method to check if the player is in the Overworld, Nether, or End
    private boolean isInValidWorld(Player player) {
        World world = player.getWorld();
        return world.getEnvironment() == World.Environment.NORMAL // Overworld
            || world.getEnvironment() == World.Environment.NETHER // Nether
            || world.getEnvironment() == World.Environment.THE_END; // End
    }

    // Reward the player with flame charges
    private void rewardFlameCharges(Player player) {
        Random random = new Random();
        int amount = 1 + random.nextInt(3); // Gives between 1 and 3 flame charges

        ItemStack flameCharge = new ItemStack(Material.FIRE_CHARGE, amount);
        ItemMeta meta = flameCharge.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Flame Charge");
            meta.getPersistentDataContainer().set(Keys.FLAME_CHARGE, PersistentDataType.BOOLEAN, true);
            flameCharge.setItemMeta(meta);
        }

        player.getInventory().addItem(flameCharge);
        player.sendMessage(ChatColor.GREEN + "You have received " + amount + " Flame Charge(s) for playing 1 hour!");
    }

    // Event to give pending flame charges when the player switches to a valid world
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (pendingFlameCharges.containsKey(playerId) && isInValidWorld(player)) {
            int pendingAmount = pendingFlameCharges.get(playerId);

            ItemStack flameCharge = new ItemStack(Material.FIRE_CHARGE, pendingAmount);
            ItemMeta meta = flameCharge.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "Flame Charge");
                meta.getPersistentDataContainer().set(Keys.FLAME_CHARGE, PersistentDataType.BOOLEAN, true);
                flameCharge.setItemMeta(meta);
            }

            player.getInventory().addItem(flameCharge);
            player.sendMessage(ChatColor.GREEN + "You have received " + pendingAmount + " Flame Charge(s) for returning to a valid world!");
            pendingFlameCharges.remove(playerId); // Clear pending charges after giving them
        }
    }
    


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name> <amount>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
            return true;
        }

        ItemStack flameCharge = new ItemStack(Material.FIRE_CHARGE, amount);
        ItemMeta meta = flameCharge.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Flame Charge");
            meta.getPersistentDataContainer().set(Keys.FLAME_CHARGE, PersistentDataType.BOOLEAN, true);
            flameCharge.setItemMeta(meta);
        }

        target.getInventory().addItem(flameCharge);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Flame Charge(s) to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Flame Charge(s).");

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();
        if (item != null && item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            NamespacedKey key = Keys.FLAME_CHARGE;
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                // Cancel the event to prevent placing the custom item
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
