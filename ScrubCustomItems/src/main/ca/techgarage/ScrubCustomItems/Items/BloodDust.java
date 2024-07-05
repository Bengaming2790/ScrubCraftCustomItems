package main.ca.techgarage.ScrubCustomItems.Items;

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
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command!");
			
			return true;
		}
	
	
		Player player = (Player) sender;
		ItemStack bloodDust = new ItemStack(Material.REDSTONE);
		ItemMeta meta = bloodDust.getItemMeta();
		
		meta.setDisplayName(ChatColor.RED + "Blood Dust");
		
		
		meta.getPersistentDataContainer().set(Keys.BLOOD_DUST, PersistentDataType.BOOLEAN, true);
		
		
		
		bloodDust.setItemMeta(meta);
		player.getInventory().addItem(bloodDust);
		
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
