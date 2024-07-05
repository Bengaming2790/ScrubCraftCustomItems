package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;

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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;
import net.md_5.bungee.api.ChatColor;

public class ServerHeart implements CommandExecutor, Listener{
	


	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only Players can use this command!");
			
			return true;
		}
	
	
		Player player = (Player) sender;
		ItemStack serverHeart = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = serverHeart.getItemMeta();
		SkullMeta skullMeta = (SkullMeta) serverHeart.getItemMeta(); 
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("162a8670-42a4-4403-9eca-94534b2b9ac7")); 
		meta.setDisplayName(ChatColor.RED + "Server Heart");
		meta.setLore(Arrays.asList(ChatColor.GRAY + "A Still Beating Bloody Heart"));
		
		
		meta.getPersistentDataContainer().set(Keys.SERVER_HEART, PersistentDataType.BOOLEAN, true);
		
		
		
		serverHeart.setItemMeta(meta);
		player.getInventory().addItem(serverHeart);
		
		return true;
	}
	 
	@EventHandler
	    public void onBlockPlace(BlockPlaceEvent event) {
	        ItemStack item = event.getItemInHand();
	        if (item != null && item.hasItemMeta()) {
	            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
	            NamespacedKey key = Keys.SERVER_HEART;
	            if (container.has(key, PersistentDataType.BOOLEAN)) {
	                // Cancel the event to prevent placing the custom item
	                event.setCancelled(true);
	            }
	        }
	    }
}