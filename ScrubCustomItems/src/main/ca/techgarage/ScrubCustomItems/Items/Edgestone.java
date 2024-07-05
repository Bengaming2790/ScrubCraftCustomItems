package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import net.md_5.bungee.api.ChatColor;

public class Edgestone implements CommandExecutor, Listener{
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command!");
			
			return true;
		}
	
	
		 Player player = (Player) sender;
	     ItemStack Edgestone = new ItemStack(Material.BEDROCK);
	     ItemMeta meta = Edgestone.getItemMeta();

	     meta.setDisplayName(ChatColor.GOLD + "Edgestone");
	     meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "A Rare Material"));

	     meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
	     meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
	     meta.getPersistentDataContainer().set(Keys.EDGESTONE, PersistentDataType.BOOLEAN, true);

	     Edgestone.setItemMeta(meta);
	     player.getInventory().addItem(Edgestone);

	        return true;
	}
	
	
	 @EventHandler
	    public void onBlockPlace(BlockPlaceEvent event) {
	        ItemStack item = event.getItemInHand();
	        if (item != null && item.hasItemMeta()) {
	            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
	            NamespacedKey key = Keys.EDGESTONE;
	            if (container.has(key, PersistentDataType.BOOLEAN)) {
	                // Cancel the event to prevent placing the custom item
	                event.setCancelled(true);
	            }
	        }
	    }
}
