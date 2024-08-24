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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import main.ca.techgarage.ScrubCustomItems.Main;

public class FlameAura implements Listener, CommandExecutor  {

    private final Main plugin;

    public FlameAura(Main plugin) {
        this.plugin = plugin;
    }


    // Method to create the aura item
    public ItemStack createAuraItem() {
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK); // Change to desired item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("&6Flame Aura"); // Change color and name as desired
            item.setItemMeta(meta);
        }
        return item;
    }

    // Event handler to start and stop the particle task
    @EventHandler
    public void onInventoryChange(PlayerInventory event) {
        Player player = ((OfflinePlayer) event).getPlayer();
        boolean hasAuraItem = false;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(createAuraItem())) { // Check if item is similar to the aura item
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

    // Method to start the particle effect task
    private void startParticleTask(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isOnline() && hasAuraItem(player)) {
                player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 10); // Adjust particle type and count as needed
            }
        }, 0L, 20L); // Runs every second
    }

    // Method to stop the particle effect task
    private void stopParticleTask(Player player) {
        // Implementation to stop the task if needed
    }

    // Check if player has the aura item
    private boolean hasAuraItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.isSimilar(createAuraItem())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("getaura")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                ItemStack auraItem = createAuraItem();
                player.getInventory().addItem(auraItem);
                player.sendMessage("You have been given an Aura item!");
                return true;
            } else {
                sender.sendMessage("This command can only be executed by a player.");
                return false;
            }
        }
        return false;
    }
}
