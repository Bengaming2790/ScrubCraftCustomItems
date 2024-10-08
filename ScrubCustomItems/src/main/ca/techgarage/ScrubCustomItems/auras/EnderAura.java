package main.ca.techgarage.ScrubCustomItems.auras;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;
import net.md_5.bungee.api.ChatColor;

public class EnderAura implements Listener, CommandExecutor  {

    private final Main plugin;

    public EnderAura(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ItemStack result = event.getResult();
        if (result != null && result.isSimilar(createAuraItem())) {
            event.setResult(null); // Prevent the item from being used in the anvil
        }
    }

    // Method to create the aura item
    public ItemStack createAuraItem() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK); // Change to desired item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE +"End Rod Aura"); // Change color and name as desired
            meta.getPersistentDataContainer().set(Keys.ENDER_AURA, PersistentDataType.BOOLEAN, true); // Add FLAME_AURA key
            item.setItemMeta(meta);
        }
        return item;
    }

    // Event handler to start and stop the particle task when inventory changes
    @EventHandler
    public void onInventoryChange(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            boolean hasAuraItem = false;

            // Check if the player has the aura item in their inventory
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && hasAuraKey(item)) {
                    hasAuraItem = true;
                    break;
                }
            }

            if (hasAuraItem) {
                startParticleTask(player);
            } else {
                stopParticleTask(player);
            }
        }
    }

    // Method to start the particle effect task
    private void startParticleTask(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isOnline() && hasAuraItem(player)) {
                // Center of the circle (player's location)
                double centerX = player.getLocation().getX();
                double centerY = player.getLocation().getY() + 1; // Adjust for player height
                double centerZ = player.getLocation().getZ();
                
                // Radius of the circle
                double radius = 1.5; // Adjust radius as needed

                // Spawn particles in a circular pattern
                for (int i = 0; i < 360; i += 20) { // Change step size for more/less particles
                    double angle = i * Math.PI / 180;
                    double x = centerX + (radius * Math.cos(angle));
                    double z = centerZ + (radius * Math.sin(angle));
                    player.getWorld().spawnParticle(Particle.END_ROD, x, centerY, z, 0); // Adjust particle count if needed
                }
            }
        }, 0L, 1L); // Runs every tick
    }

    // Method to stop the particle effect task (if needed)
    private void stopParticleTask(Player player) {
        // No task stopping logic needed in this case, but can be implemented if necessary
    }

    // Check if an item has the FLAME_AURA key
    private boolean hasAuraKey(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().has(Keys.ENDER_AURA, PersistentDataType.BOOLEAN);
        }
        return false;
    }

    // Check if the player has the aura item with the FLAME_AURA key
    private boolean hasAuraItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && hasAuraKey(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("endrodaura")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack auraItem = createAuraItem();
                player.getInventory().addItem(auraItem);
                player.sendMessage(ChatColor.GREEN + "You have been given an End Rod Aura!");
                return true;
            } else {
                sender.sendMessage("This command can only be executed by a player.");
                return false;
            }
        }
        return false;
    }
}
