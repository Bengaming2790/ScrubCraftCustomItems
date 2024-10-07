package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class FreezeClock implements CommandExecutor, Listener {

    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final long COOLDOWN_TIME = 1000; // 15 seconds in milliseconds

    // Method to create the Freeze Clock item
    public ItemStack createFreezeClock() {
        ItemStack fc = new ItemStack(Material.CLOCK);
        ItemMeta meta = fc.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Freeze Clock");
            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Use to freeze your foes in place",
            		"has a 15 second cooldown and 20 uses"));
            meta.getPersistentDataContainer().set(Keys.FREEZE_CLOCK, PersistentDataType.INTEGER, 20); // Set initial usage count to 20

            fc.setItemMeta(meta);
        }

        return fc;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.CLOCK && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Check if the player has the required permission
            if (!player.hasPermission("sc.magic")) {
                player.sendMessage(ChatColor.RED + "The magic of the gods does not work here");
                return;
            }

            if (container.has(Keys.FREEZE_CLOCK, PersistentDataType.INTEGER)) {
                int usesLeft = container.get(Keys.FREEZE_CLOCK, PersistentDataType.INTEGER);

                // Check for cooldown
                if (cooldowns.containsKey(player.getUniqueId())) {
                    long lastUsed = cooldowns.get(player.getUniqueId());
                    if (System.currentTimeMillis() - lastUsed < COOLDOWN_TIME) {
                        player.sendMessage(ChatColor.RED + "You must wait before using the Freeze Clock again.");
                        return;
                    }
                }

                // Apply effects
                if (event.getAction().toString().contains("RIGHT_CLICK")) {
                    for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 255)); // 3 seconds, with high level of slowness
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 60, 255)); // 3 seconds, with high level of mining fatigue
                        }
                    }

                    // Update usage count
                    usesLeft--;
                    container.set(Keys.FREEZE_CLOCK, PersistentDataType.INTEGER, usesLeft);
                    item.setItemMeta(meta);

                    // Check if item should break
                    if (usesLeft <= 0) {
                        player.getInventory().remove(item);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        player.sendMessage(ChatColor.RED + "Your Freeze Clock has broken.");
                    } else {
                        // Set cooldown
                        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());

                        // Notify the player
                        player.sendMessage(ChatColor.GREEN + "You have used the Freeze Clock!");
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        ItemStack fc = createFreezeClock();
        target.getInventory().addItem(fc);
        sender.sendMessage(ChatColor.GREEN + "Gave a Freeze Clock to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Freeze Clock.");

        return true;
    }
}
