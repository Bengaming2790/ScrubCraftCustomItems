package main.ca.techgarage.ScrubCustomItems.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import main.ca.techgarage.ScrubCustomItems.Main;
import net.md_5.bungee.api.ChatColor;

public class SetSpawnPointCommand implements CommandExecutor {

    private final Main plugin;

    public SetSpawnPointCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();
        String locationString = location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch() + "," + location.getWorld().getName();
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(plugin.getSpawnPointKey(), PersistentDataType.STRING, locationString);

        player.sendMessage(ChatColor.GREEN + "Spawn point set to your current location!");

        return true;
    }
}
