package main.ca.techgarage.ScrubCustomItems.commands;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import main.ca.techgarage.ScrubCustomItems.Main;

public class TeleportPaperGUI implements Listener, CommandExecutor {
    private final Inventory adminInventory;
    private final Main plugin;

    // Constructor
    public TeleportPaperGUI(Main plugin) {
        this.plugin = plugin;
        adminInventory = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Teleport Paper Storage");
    }

    // Open the inventory for a player
    public void openInventory(Player player) {
        player.openInventory(adminInventory);
    }

    // Event: Handle inventory clicks
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Teleport Paper Storage")) {
            event.setCancelled(true); // Prevent direct item manipulation

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            if (event.getAction().toString().contains("PLACE")) {
                // Allow adding any item
                player.sendMessage(ChatColor.GREEN + "Item added to the Teleport Paper Storage.");
            }

            if (event.getAction().toString().contains("PICKUP")) {
                // Allow removal but duplicate the item
                if (clickedItem != null) {
                    player.getInventory().addItem(clickedItem.clone());
                    player.sendMessage(ChatColor.GREEN + "You have taken an item from the Teleport Paper Storage.");
                }
            }
        }
    }

    // Event: Save inventory contents when closed
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals(ChatColor.DARK_PURPLE + "Teleport Paper Storage")) {
            saveInventoryToConfig();
        }
    }

    // Add an item to the storage
    public void addItem(ItemStack item) {
        if (item != null) {
            HashMap<Integer, ItemStack> failedItems = adminInventory.addItem(item);

            if (!failedItems.isEmpty()) {
                Bukkit.getLogger().warning("Failed to add item because the inventory is full.");
            } else {
                Bukkit.getLogger().info("Item added to inventory.");
            }
        } else {
            Bukkit.getLogger().warning("Null item was not added.");
        }
    }

    // Save inventory contents to the config
    public void saveInventoryToConfig() {
        ItemStack[] contents = adminInventory.getContents();
        plugin.getConfig().set("swift_sneak_items", contents);
        plugin.saveConfig();
        Bukkit.getLogger().info("Saved items to config.");
    }

    // Load inventory contents from the config
    public void loadInventoryFromConfig() {
        List<?> serializedItems = plugin.getConfig().getList("swift_sneak_items");
        if (serializedItems != null) {
            adminInventory.setContents(serializedItems.toArray(new ItemStack[0]));
        } else {
            Bukkit.getLogger().warning("No items found in the configuration!");
        }
    }

    // Command: Open the inventory (called by Main class)
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("sc.customitems.openswiftsneakstorage")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to open the Swift Sneak storage.");
                return false;
            }
            openInventory(player);
            player.sendMessage(ChatColor.GREEN + "Teleport Paper Storage opened.");
            return true;
        }
        return false;
    }
}