package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import main.ca.techgarage.ScrubCustomItems.Keys;

public class Edgestone implements CommandExecutor, Listener {

    public static ItemStack createEdgestone() {
        ItemStack edgestone = new ItemStack(Material.BEDROCK);
        ItemMeta meta = edgestone.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Edgestone");
            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "A Rare Material"));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.getPersistentDataContainer().set(Keys.EDGESTONE, PersistentDataType.BOOLEAN, true);

            edgestone.setItemMeta(meta);
        }
        return edgestone;
    }

    public static ShapelessRecipe getEdgestoneRecipe(JavaPlugin plugin) {
        ItemStack edgestone = createEdgestone();
        NamespacedKey key = new NamespacedKey(plugin, "edgestone");

        ShapelessRecipe recipe = new ShapelessRecipe(key, edgestone);
        recipe.addIngredient(4, Material.PLAYER_HEAD);

        return recipe;
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;

        ItemStack result = event.getRecipe().getResult();
        if (result == null || !result.isSimilar(createEdgestone())) return;

        boolean validRecipe = true;

        for (ItemStack item : event.getInventory().getMatrix()) {
            if (item != null && item.getType() == Material.PLAYER_HEAD) {
                ItemMeta meta = item.getItemMeta();
                if (meta == null || !meta.getPersistentDataContainer().has(Keys.SERVER_HEART, PersistentDataType.BOOLEAN)) {
                    validRecipe = false;
                    break;
                }
            }
        }

        if (!validRecipe) {
            event.getInventory().setResult(null);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name> <amount>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Amount must be a number.");
            return true;
        }

        ItemStack edgestone = new ItemStack(Material.BEDROCK, amount);
        ItemMeta meta = edgestone.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Edgestone");
            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "A Rare Material"));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.getPersistentDataContainer().set(Keys.EDGESTONE, PersistentDataType.BOOLEAN, true);

            edgestone.setItemMeta(meta);
        }

        target.getInventory().addItem(edgestone);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Edgestone(s) to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Edgestone(s)");

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        Player player = event.getPlayer();
        if (item != null && item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            NamespacedKey key = Keys.EDGESTONE;
           
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                // Cancel the event to prevent placing the custom item
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }
}
