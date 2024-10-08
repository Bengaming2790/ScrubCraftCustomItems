package main.ca.techgarage.ScrubCustomItems.scythes;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class Swift implements CommandExecutor, Listener {
    
    public ItemStack createScythe() {
        ItemStack swift = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = icicle.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Swift Scythe");
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_speed", 2, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", 2.5, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 
                new AttributeModifier(UUID.randomUUID(), "player.entity_interaction_range", 0.25, AttributeModifier.Operation.ADD_NUMBER));
            meta.getPersistentDataContainer().set(Keys.SWIFT_SCYTHE, PersistentDataType.BOOLEAN, true);

            swift.setItemMeta(meta);
        }
        
        return swift;
    }

    public void addSScytheRecipe(Main plugin) {
        ItemStack scythe = createScythe();

        // Changed the key to be unique
        NamespacedKey key = new NamespacedKey(plugin, "swiftscythe");
        ShapedRecipe recipe = new ShapedRecipe(key, scythe);
        recipe.shape("NNN", "MSB", "MSM");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('B', Material.BEDROCK);
        recipe.setIngredient('M', Material.RABBIT_FOOT);

        Bukkit.addRecipe(recipe);
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

        ItemStack swift = createScythe();
        target.getInventory().addItem(swift);
        sender.sendMessage(ChatColor.GREEN + "Gave the Swift Scythe to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Swift Scythe.");
        return true;
    }

  // Add speed to the player when item is in their MAIN HAND


}
