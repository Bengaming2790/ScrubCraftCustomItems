package main.ca.techgarage.ScrubCustomItems.Items;

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
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

 // Import your main class

public class Hammer implements CommandExecutor, Listener {
    
    // Method to create the Scythe item
    public ItemStack createScythe() {
        ItemStack scythe = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = scythe.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Scythe");
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_speed", 1.8, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, 
                new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", 3, AttributeModifier.Operation.ADD_NUMBER));
            meta.addAttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 
                new AttributeModifier(UUID.randomUUID(), "player.entity_interaction_range", 0.5, AttributeModifier.Operation.ADD_NUMBER));
            meta.getPersistentDataContainer().set(Keys.BASIC_SCYTHE, PersistentDataType.BOOLEAN, true);

            scythe.setItemMeta(meta);
        }
        
        return scythe;
    }

    // Method to add the crafting recipe for the Scythe
    public void addScytheRecipe(Main plugin) {
        ItemStack scythe = createScythe();

        NamespacedKey key = new NamespacedKey(plugin, "scythe");
        ShapedRecipe recipe = new ShapedRecipe(key, scythe);
        recipe.shape("NTN", "BSB", " S ");
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('T', Material.NETHERITE_BLOCK);
        recipe.setIngredient('S', Material.STICK);
        recipe.setIngredient('B', Material.BEDROCK);

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

        ItemStack scythe = createScythe();
        target.getInventory().addItem(scythe);
        sender.sendMessage(ChatColor.GREEN + "Gave a Basic Scythe to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Scythe.");

        return true;
    }
}
