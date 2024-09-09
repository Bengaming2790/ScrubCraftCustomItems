package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import main.ca.techgarage.ScrubCustomItems.Keys;

public class harmingstick implements CommandExecutor, Listener {
    
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

        ItemStack harmingStick = new ItemStack(Material.STICK);
        ItemMeta meta = harmingStick.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_RED + "Harming Stick");
            meta.setLore(Arrays.asList("Does Increased Damage Through Armour"));

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.HARMING_STICK, PersistentDataType.BOOLEAN, true);

            harmingStick.setItemMeta(meta);
        }

        target.getInventory().addItem(harmingStick);
        sender.sendMessage(ChatColor.GREEN + "Gave a Harming Stick to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received a Harming Stick.");

        return true;
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Entity damaged = event.getEntity();
            ItemStack item = damager.getInventory().getItemInMainHand();

            if (item != null && item.getType() == Material.STICK) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    PersistentDataContainer container = meta.getPersistentDataContainer();
                    if (container.has(Keys.HARMING_STICK, PersistentDataType.BOOLEAN)) {
                        if (damaged instanceof Player) {
                            Player target = (Player) damaged;
                            target.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0)); // Instant Damage 1
                        }
                    }
                }
            }
        }
    }
}
