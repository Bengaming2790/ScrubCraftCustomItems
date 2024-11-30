package main.ca.techgarage.ScrubCustomItems.scythes;

import java.util.Arrays;
import java.util.HashMap;
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
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class Ghast implements  Listener, CommandExecutor {
	  private HashMap<UUID, Long> cooldowns = new HashMap<>();

	    public ItemStack createScythe() {
	        ItemStack breeze = new ItemStack(Material.NETHERITE_SWORD);
	        ItemMeta meta = breeze.getItemMeta();

	        if (meta != null) {
	            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ghast Scythe");
	            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Right Click Interactable"));
	            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, 
	                new AttributeModifier(UUID.randomUUID(), "generic.attack_speed", 2, AttributeModifier.Operation.ADD_NUMBER));
	            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, 
	                new AttributeModifier(UUID.randomUUID(), "generic.attack_damage", 4, AttributeModifier.Operation.ADD_NUMBER));
	            meta.addAttributeModifier(Attribute.PLAYER_ENTITY_INTERACTION_RANGE, 
	                new AttributeModifier(UUID.randomUUID(), "player.entity_interaction_range", 0.5, AttributeModifier.Operation.ADD_NUMBER));
	            meta.getPersistentDataContainer().set(Keys.GHAST_SCYTHE, PersistentDataType.BOOLEAN, true);
	            breeze.setItemMeta(meta);
	        }
	        
	        return breeze;
	    }

	    public void addIScytheRecipe(Main plugin) {
	        ItemStack scythe = createScythe();

	        // Changed the key to be unique
	        NamespacedKey key = new NamespacedKey(plugin, "ghast_scythe");
	        ShapedRecipe recipe = new ShapedRecipe(key, scythe);
	        recipe.shape("NNN", "ISB", "ISF");
	        recipe.setIngredient('N', Material.NETHERITE_INGOT);
	        recipe.setIngredient('S', Material.STICK);
	        recipe.setIngredient('B', Material.BEDROCK);
	        recipe.setIngredient('I', Material.GHAST_TEAR);
	        recipe.setIngredient('F', Material.FIRE_CHARGE);

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

	        ItemStack breeze = createScythe();
	        target.getInventory().addItem(breeze);
	        sender.sendMessage(ChatColor.GREEN + "Gave the Ghast Scythe to " + target.getName());
	        target.sendMessage(ChatColor.GREEN + "You have received the Ghast Scythe.");

	        return true;
	    }
	    
	    @EventHandler
	    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
	        if (event.getDamager() instanceof Player) {
	            Player player = (Player) event.getDamager();
	            ItemStack weapon = player.getInventory().getItemInMainHand();

	            // Check if the player has the required permission
	            if (weapon.hasItemMeta() && weapon.getItemMeta().getPersistentDataContainer().has(Keys.GHAST_SCYTHE, PersistentDataType.BOOLEAN) && !player.hasPermission("sc.scythe.attack")) {
	                player.sendMessage(ChatColor.RED + "The magic of the scythe's do not work here!");
	                return;
	            }

	            if (weapon.hasItemMeta() && weapon.getItemMeta().getPersistentDataContainer().has(Keys.GHAST_SCYTHE, PersistentDataType.BOOLEAN)) {
	                Entity target = event.getEntity();

	                if (target instanceof LivingEntity) {
	                    LivingEntity livingTarget = (LivingEntity) target;

	                    // Knockback logic
	                    double knockbackMultiplier = 1.5; // Extra knockback amount
	                    // Calculate the direction and apply knockback
	                    livingTarget.setVelocity(livingTarget.getVelocity().add(player.getLocation().getDirection().multiply(knockbackMultiplier)));

	                }
	            }
	        }
	    }


	    @EventHandler
	    public void onRightClick(PlayerInteractEvent event) {
	        Player player = event.getPlayer();
	        ItemStack weapon = player.getInventory().getItemInMainHand();

	        // Check if the player right-clicked with the Breeze Scythe and has the correct permission
	        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
	            weapon.hasItemMeta() && 
	            weapon.getItemMeta().getPersistentDataContainer().has(Keys.GHAST_SCYTHE, PersistentDataType.BOOLEAN) &&
	            player.hasPermission("sc.scythe.attack")) {

	            // Get the player's UUID and current time
	            UUID playerId = player.getUniqueId();
	            long currentTime = System.currentTimeMillis();

	            // Check if the player is on cooldown
	            if (cooldowns.containsKey(playerId)) {
	                long lastUseTime = cooldowns.get(playerId);
	                if (currentTime - lastUseTime < 500) { //500ms = 0.5s
	                    return;
	                }
	            }

	            // Update the player's last use time to the current time
	            cooldowns.put(playerId, currentTime);

	            // Spawn the WindCharge entity (same as Wither Skull)
	            Fireball fireBall = player.getWorld().spawn(player.getEyeLocation(), LargeFireball.class);

	            // Set the velocity of the WindCharge in the direction the player is facing
	            Vector direction = player.getLocation().getDirection().multiply(2); // Adjust multiplier for speed
	            fireBall.setVelocity(direction);

	            // Deal 10 durability damage to the weapon
	            if (weapon.getType().getMaxDurability() - weapon.getDurability() > 10) {
	                weapon.setDurability((short) (weapon.getDurability() + 10));
	            } else {
	                player.getInventory().removeItem(weapon); // Remove item if it breaks
	                player.sendMessage(ChatColor.RED + "Your Ghast Scythe has broken!");
	            }
	        }
	    }
}
