package main.ca.techgarage.ScrubCustomItems.Items;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import main.ca.techgarage.ScrubCustomItems.Keys;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class ClaimLamp implements CommandExecutor, Listener {

    private final HashMap<UUID, String> lampRegions = new HashMap<>();

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

        ItemStack claimlamp = new ItemStack(Material.REDSTONE_LAMP, amount);
        ItemMeta meta = claimlamp.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.DARK_PURPLE + "Claim Lamp");
            meta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Place to claim the area around it.",
            		"This item will only work within 10k!!!",
            		"Main hand usage is required to claim!"));

            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.getPersistentDataContainer().set(Keys.CLAIM_LAMP, PersistentDataType.BOOLEAN, true);

            claimlamp.setItemMeta(meta);
        }

        target.getInventory().addItem(claimlamp);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + " Claim Lamp(s) to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " Claim Lamp(s)");

        return true;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Location loc = block.getLocation();
        ItemStack weapon = player.getInventory().getItemInMainHand();
        ItemStack item = player.getInventory().getItemInOffHand();

        if (weapon.hasItemMeta() && weapon.getItemMeta().getPersistentDataContainer().has(Keys.CLAIM_LAMP, PersistentDataType.BOOLEAN) && player.hasPermission("sc.scythe.attack")){
        	 // Create a valid region ID using the world name and coordinates
            String regionId = String.format("claim_lamp_%s_%d_%d_%d", loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            // Define the corners of the protected region, 4 chunk radius (32 blocks in each direction)
            BlockVector3 lowerCorner = BlockVector3.at(loc.getBlockX() - 32, loc.getBlockY(), loc.getBlockZ() - 32);
            BlockVector3 upperCorner = BlockVector3.at(loc.getBlockX() + 32, loc.getBlockY() + 256, loc.getBlockZ() + 32);

            // Create the protected region using the sanitized region ID
            ProtectedRegion region = new ProtectedCuboidRegion(regionId, lowerCorner, upperCorner);

            // Access the WorldGuard API and register the region
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = container.get(WorldGuardPlugin.inst().wrapPlayer(player).getWorld());

            if (regionManager != null) {
                try {
                    // Set flags for the region (customize these as needed)
                    region.setFlag(Flags.BUILD, StateFlag.State.DENY); // Deny building in this region
                    region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY); // Deny Block Breaking in this region

                    // Add the player as an owner of the region
                    region.getOwners().addPlayer(player.getUniqueId());

                    // Add the region to the WorldGuard manager
                    regionManager.addRegion(region);

                    // Optionally, save the regions to disk
                    regionManager.save();
                } catch (Exception e) {
                    e.printStackTrace();
                    player.sendMessage("§cAn error occurred while creating the protected region!");
                }
            } else {
                player.sendMessage("§cCould not get region manager for this world!");
            }
        }
        
        
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.REDSTONE_LAMP) {
            // Check if this is a Claim Lamp
            Player player = event.getPlayer();
            if (lampRegions.containsKey(player.getUniqueId())) {
                String regionId = lampRegions.get(player.getUniqueId());

                // Get WorldGuard region manager
                RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(event.getBlock().getWorld()));
                if (regionManager != null) {
                    regionManager.removeRegion(regionId);
                    player.sendMessage(ChatColor.RED + "Your claim has been removed as the lamp was destroyed.");
                }
            }
        }
    }
}
