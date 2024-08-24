package main.ca.techgarage.ScrubCustomItems.Items;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import main.ca.techgarage.ScrubCustomItems.Keys;

public class ServerHeart implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name> <amount>");
            return false;
        }

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
            onlinePlayer.sendMessage(ChatColor.GREEN + "You have received " + amount + " Server Heart(s).");
        } else {
            // Add the item to the player's offline inventory
            // This requires a plugin that can handle offline player inventories, as Bukkit does not support this directly.
        }

        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Server Heart(s) to " + target.getName());

        return true;
    }

    // Method to create a player head ItemStack of "ServerHeart" with custom texture
    ItemStack getPlayerHead() {
        String base64 = "ewogICJ0aW1lc3RhbXAiIDogMTcyMTIzOTU0MjQwOSwKICAicHJvZmlsZUlkIiA6ICIwZTFmY2NlZWFlMDg0MGVjYWExMmFkODE4NjVhYTI4ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJTZXJ2ZXJIZWFydCIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xOWEzYjVmYWIwMWRmYWJmMWY4YTgxNDQ0MGE4Y2U4MGRlMTA5YjEzNWE1MWFlZjIyNWM5ZWU5OGZlNDQ3ZTAiCiAgICB9CiAgfQp9";
        GameProfile profile = new GameProfile(UUID.randomUUID(), "ServerHeart");
        profile.getProperties().put("textures", new Property("textures", base64));

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        meta.setDisplayName(ChatColor.RED + "Server Heart");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "A Still Beating Bloody Heart"));

        // Set custom data to identify the item
        meta.getPersistentDataContainer().set(Keys.SERVER_HEART, PersistentDataType.BOOLEAN, true);

        skull.setItemMeta(meta);
        return skull;
    }

    // Event handler to prevent placing the Server Heart
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item != null && item.getType() == Material.PLAYER_HEAD) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(Keys.SERVER_HEART, PersistentDataType.BOOLEAN)) {
                event.setCancelled(true);
            }
        }
    }
}
