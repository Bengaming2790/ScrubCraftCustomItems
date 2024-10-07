package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import main.ca.techgarage.ScrubCustomItems.Keys;

public class BackPacks implements CommandExecutor, Listener {

    // Store inventories by UUID
    private Map<UUID, Inventory> backpackInventories = new HashMap<>();

    public static ItemStack createBackpack() {
        ItemStack backpack = new ItemStack(Material.TRAPPED_CHEST);
        ItemMeta meta = backpack.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.BLUE + "Backpack");
            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Right Click to Open"));
            
            // Assign a unique ID to each backpack
            UUID backpackId = UUID.randomUUID();
            meta.getPersistentDataContainer().set(Keys.BACK_PACK_ID, PersistentDataType.STRING, backpackId.toString());

            backpack.setItemMeta(meta);
        }
        return backpack;
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

        for (int i = 0; i < amount; i++) {
            ItemStack backpack = createBackpack();
            target.getInventory().addItem(backpack);
        }

        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Backpack(s) to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Backpack(s)");

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(Keys.BACK_PACK_ID, PersistentDataType.STRING)) {
                event.setCancelled(true);

                String backpackIdString = container.get(Keys.BACK_PACK_ID, PersistentDataType.STRING);
                UUID backpackId = UUID.fromString(backpackIdString);

                Inventory backpackInventory = backpackInventories.getOrDefault(backpackId, Bukkit.createInventory(null, 18, "Backpack"));
                backpackInventories.putIfAbsent(backpackId, backpackInventory);

                player.openInventory(backpackInventory);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();

        if (item != null && item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            NamespacedKey key = Keys.BACK_PACK_ID;

            if (container.has(key, PersistentDataType.STRING)) {
                // Cancel the event to prevent placing the backpack
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Optional: Prevent taking out the backpack from its own inventory
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem != null && clickedItem.hasItemMeta()) {
            PersistentDataContainer container = clickedItem.getItemMeta().getPersistentDataContainer();
            if (container.has(Keys.BACK_PACK_ID, PersistentDataType.STRING)) {
                event.setCancelled(true);
            }
        }
    }
}
