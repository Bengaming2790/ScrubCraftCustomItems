package main.ca.techgarage.ScrubCustomItems.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommandExecutor implements CommandExecutor {

    private static final Location BASE_LOCATION = new Location(
        Bukkit.getWorld("world_quest"), // Replace with your target world
        309.60, // Replace with your x coordinate
        5, // Replace with your y coordinate
        276.50  // Replace with your z coordinate
    );

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Default pitch
            float pitch = 0;

            // Check if pitch is provided in command arguments
            if (args.length >= 1) {
                try {
                    pitch = Float.parseFloat(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid pitch value. It must be a number.");
                    return false;
                }
            }

            // Create a new location with yaw set to 90 (east) and the specified pitch
            Location teleportLocation = BASE_LOCATION.clone();
            teleportLocation.setPitch(pitch);
            teleportLocation.setYaw(270); // East

            player.teleport(teleportLocation);
            player.sendMessage(ChatColor.GREEN + "Learn the Rules of 10k to proceed!");
            return true;
        } else {
            sender.sendMessage("Only players can use this command.");
            return false;
        }
    }
}
