package main.ca.techgarage.ScrubCustomItems;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class broadcast implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.GREEN + "It is recommeded you play with Optifine or CIT Resewn to get the full ScrubCraft Experience!");

            } 
}
