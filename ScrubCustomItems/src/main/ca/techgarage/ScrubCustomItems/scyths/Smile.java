package main.ca.techgarage.ScrubCustomItems.scyths;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

@SuppressWarnings("unused")
public class Smile implements CommandExecutor, Listener {
    
    // Method to create the Scythe item
    public ItemStack createScythe() {
        ItemStack abscythe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = abscythe.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "The Smile's Scythe");
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_speed", 2, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", 4, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 
                new AttributeModifier(UUID.randomUUID(), "player.entity_interaction_range", 0.5, AttributeModifier.Operation.ADD_NUMBER));
            meta.getPersistentDataContainer().set(Keys.SMILE_SCYTHE, PersistentDataType.BOOLEAN, true);

            abscythe.setItemMeta(meta);
        }
        
        return abscythe;
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

        ItemStack abscythe = createScythe();
        target.getInventory().addItem(abscythe);
        sender.sendMessage(ChatColor.GREEN + "Gave the Smile's Scythe to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Smile's Scythe.");

        return true;
    }
    
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Check if the damager is a player
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack weapon = player.getInventory().getItemInMainHand();

            // Check if the player has the required permission
            if (weapon.hasItemMeta() && weapon.getItemMeta().getPersistentDataContainer().has(Keys.SMILE_SCYTHE, PersistentDataType.BOOLEAN) && !player.hasPermission("sc.scythe.attack")) {
                player.sendMessage(ChatColor.RED + "The magic of the scythe's do not work here!");
                return;
            }
            

            // Check if the item is the Abyssal Scythe
            if (weapon.hasItemMeta() && weapon.getItemMeta().getPersistentDataContainer().has(Keys.SMILE_SCYTHE, PersistentDataType.BOOLEAN)) {
                Entity target = event.getEntity();

                // Apply Wither effect and set the target on fire
                if (target instanceof LivingEntity) {
                    ((LivingEntity) target).addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0)); // Wither I for 5 seconds
                }
            }
        }
    }




    
}
