package main.ca.techgarage.ScrubCustomItems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class broadcast implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send general messages
        player.sendMessage(ChatColor.GREEN + "It is recommended you play with Optifine or CIT Resewn to get the full ScrubCraft Experience!");
        
        // Check if the player's name starts with "BR_"
        if (player.getName().startsWith("br_")) {
            player.sendMessage(ChatColor.GREEN + "While there is Bedrock Support it is better to play on the Java Edition of Minecraft!");
        }
    }
}
