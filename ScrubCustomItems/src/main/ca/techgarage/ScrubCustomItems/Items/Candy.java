package main.ca.techgarage.ScrubCustomItems.Items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import main.ca.techgarage.ScrubCustomItems.Keys;

public class Candy implements CommandExecutor, Listener {
    private HashMap<UUID, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name> <amount>");
            return false;
        }

        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || !target.hasPlayedBefore()) {
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

        // Create a player head item of "ServerHeart"
        ItemStack playerHead = getPlayerHead();

        playerHead.setAmount(amount);

        if (target.isOnline()) {
            Player onlinePlayer = target.getPlayer();
            onlinePlayer.getInventory().addItem(playerHead);
            onlinePlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " piece(s) of candy for HALLOWEEN");
        } else {
            // Add the item to the player's offline inventory
            // This requires a plugin that can handle offline player inventories, as Bukkit does not support this directly.
        }

        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " piece(s) of candy " + target.getName());

        return true;
    }

    ItemStack getPlayerHead() {
        
        ItemStack candy = new ItemStack(Material.SUGAR);
        ItemMeta meta = candy.getItemMeta();

        meta.setDisplayName(ChatColor.GOLD + "Candy 24");
        meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "ScrubCraft Halloween 2024"));

        // Set custom data to identify the item with CANDY_HEAD
        meta.getPersistentDataContainer().set(Keys.CANDY, PersistentDataType.BOOLEAN, true);

        candy.setItemMeta(meta);
        return candy;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    ItemStack item = event.getItem();

    // Check if the action is a right-click and the item is a candy
    if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
        if (item != null && item.getType() == Material.SUGAR) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(Keys.CANDY, PersistentDataType.BOOLEAN)) {
                
                // Check if player is on cooldown
                UUID playerUUID = player.getUniqueId();
                if (cooldowns.containsKey(playerUUID)) {
                    long lastUse = cooldowns.get(playerUUID);
                    if (System.currentTimeMillis() - lastUse < 60000) { // 60 seconds cooldown
                        player.sendMessage(ChatColor.RED + "You need to wait before using the candy again!");
                        return;
                    }
                }
                Random rand = new Random();
                int randomPower = rand.nextInt(5);
                // Get a random PotionEffectType
                PotionEffectType[] effects = PotionEffectType.values();
                PotionEffectType randomEffect = effects[new Random().nextInt(effects.length)];

                // Apply the random effect to the player with a duration and amplifier
                player.addPotionEffect(new PotionEffect(randomEffect, 200, randomPower)); // 200 ticks = 10 seconds

                // Send a message to the player indicating the effect applied
                player.sendMessage(ChatColor.LIGHT_PURPLE + "You feel different!");
                
                // Set the cooldown
                cooldowns.put(playerUUID, System.currentTimeMillis());
            }
        }
    }
}
    // Event handler to prevent placing the Server Heart
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(Keys.CANDY, PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
