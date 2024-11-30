package main.ca.techgarage.ScrubCustomItems.auras;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class WitchAura implements Listener, CommandExecutor {
    private final Main plugin;
    private final Map<Player, BukkitTask> particleTasks = new HashMap<>();

    public WitchAura(Main plugin) {
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
            meta.setDisplayName(ChatColor.BLUE + "Witch Aura"); // Change color and name as desired
            meta.setLore(Arrays.asList(ChatColor.GOLD + "Halloween 24 Limited Item"));
            meta.getPersistentDataContainer().set(Keys.WITCH_AURA, PersistentDataType.BOOLEAN, true); // Add WITCH_AURA key
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryChange(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            updateParticleEffect(player);
        }
    }

    // Start particles on player join
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateParticleEffect(player);
    }

    // Stop particles on player quit
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        stopParticleTask(player);
    }

    // Check if the player has the aura item and start/stop particles
    private void updateParticleEffect(Player player) {
        if (hasAuraItem(player)) {
            startParticleTask(player);
        } else {
            stopParticleTask(player);
        }
    }

    // Start particle task
    private void startParticleTask(Player player) {
        // Avoid creating multiple tasks for the same player
        if (particleTasks.containsKey(player)) return;

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (player.isOnline() && hasAuraItem(player)) {
                double centerX = player.getLocation().getX();
                double centerY = player.getLocation().getY() + 1;
                double centerZ = player.getLocation().getZ();
                
                double radius = 1.5;

                for (int i = 0; i < 360; i += 20) {
                    double angle = i * Math.PI / 180;
                    double x = centerX + (radius * Math.cos(angle));
                    double z = centerZ + (radius * Math.sin(angle));
                    player.getWorld().spawnParticle(Particle.WITCH, x, centerY, z, 0);
                }
            }
        }, 0L, 1L); // Adjust the interval as needed

        particleTasks.put(player, task);
    }

    // Stop particle task
    private void stopParticleTask(Player player) {
        BukkitTask task = particleTasks.remove(player);
        if (task != null) {
            task.cancel();
        }
    }

    // Check if an item has the WITCH_AURA key
    private boolean hasAuraKey(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.getPersistentDataContainer().has(Keys.WITCH_AURA, PersistentDataType.BOOLEAN);
        }
        return false;
    }

    // Check if the player has the aura item with the WITCH_AURA key
    private boolean hasAuraItem(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && hasAuraKey(item)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player name>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        ItemStack auraItem = createAuraItem();
        target.getInventory().addItem(auraItem);
        sender.sendMessage(ChatColor.GREEN + "Gave a Witch Aura to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Witch Aura!");

        return true;
    }

}
