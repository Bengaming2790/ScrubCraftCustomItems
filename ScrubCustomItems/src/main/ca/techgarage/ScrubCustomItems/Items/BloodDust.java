package main.ca.techgarage.ScrubCustomItems.Items;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import net.md_5.bungee.api.ChatColor;

public class BloodDust implements CommandExecutor, Listener{

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

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be greater than zero.");
            return true;
        }

        ItemStack bloodDust = new ItemStack(Material.REDSTONE, amount);
        ItemMeta meta = bloodDust.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "Blood Dust");
            meta.getPersistentDataContainer().set(Keys.BLOOD_DUST, PersistentDataType.BOOLEAN, true);
            bloodDust.setItemMeta(meta);
        }

        target.getInventory().addItem(bloodDust);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Blood Dust to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Blood Dust.");

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item != null && item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            NamespacedKey key = Keys.BLOOD_DUST;
            if (container.has(key, PersistentDataType.BOOLEAN)) {
                // Cancel the event to prevent placing the custom item
                event.setCancelled(true);
            }
        }
    }
}
