package main.ca.techgarage.ScrubCustomItems.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import main.ca.techgarage.ScrubCustomItems.Main;
import main.ca.techgarage.ScrubCustomItems.Keys;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class CustomItemCommands implements CommandExecutor, Listener {

    private final Main plugin;
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    public CustomItemCommands(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack spawnPaper = new ItemStack(Material.PAPER);
        ItemMeta meta = spawnPaper.getItemMeta();

        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Spawn Teleport Paper");
        meta.setLore(Arrays.asList(
                "Unlimited Use Teleport Paper",
                ChatColor.GRAY + "Right click to teleport to the Spawn Tower"));

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);

        meta.getPersistentDataContainer().set(Keys.SPAWN_PAPER, PersistentDataType.BOOLEAN, true);

        spawnPaper.setItemMeta(meta);
        player.getInventory().addItem(spawnPaper);

        return true;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(Keys.SPAWN_PAPER, PersistentDataType.BOOLEAN)) {
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();

            if (cooldowns.containsKey(playerId) && cooldowns.get(playerId) > currentTime) {
                player.sendMessage(ChatColor.RED + "You can't use this paper yet!");
                return;
            }

            PersistentDataContainer data = player.getPersistentDataContainer();
            if (data.has(plugin.getSpawnPointKey(), PersistentDataType.STRING)) {
                String spawnPointData = data.get(plugin.getSpawnPointKey(), PersistentDataType.STRING);
                String[] parts = spawnPointData.split(",");
                if (parts.length == 6) {
                    double x = Double.parseDouble(parts[0]);
                    double y = Double.parseDouble(parts[1]);
                    double z = Double.parseDouble(parts[2]);
                    float yaw = Float.parseFloat(parts[3]);
                    float pitch = Float.parseFloat(parts[4]);
                    String worldName = parts[5];
                    Location spawnLocation = new Location(player.getServer().getWorld(worldName), x, y, z, yaw, pitch);

                    player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 160, 1)); // Add nausea effect for 8 seconds
                    player.sendMessage(ChatColor.GREEN + "Teleporting to Spawn in 5 seconds");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.teleport(spawnLocation);
                            player.removePotionEffect(PotionEffectType.NAUSEA);
                            player.sendMessage(ChatColor.GREEN + "Teleported to the Spawn Tower!");

                            // Set cooldown to 10 seconds
                            cooldowns.put(playerId, currentTime + 10000);
                        }
                    }.runTaskLater(plugin, 100L); // 100 ticks = 5 seconds
                }
            } else {
                player.sendMessage(ChatColor.RED + "Spawn point is not set!");
            }
        }
    }
}
