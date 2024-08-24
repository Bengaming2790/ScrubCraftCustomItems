package main.ca.techgarage.ScrubCustomItems.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import main.ca.techgarage.ScrubCustomItems.Main;
import org.bukkit.NamespacedKey;

public class LimitedTeleportPaperCreate implements CommandExecutor, Listener {

    private final Main plugin;

    public LimitedTeleportPaperCreate(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("limitedteleportpapercreate")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("sc.customitems.limitedteleportpapercreate")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }

            String locationName = args.length > 0 ? args[0].replace('_', ' ') : "Teleport Location";
            String paperName = ChatColor.DARK_PURPLE + locationName + " Limited Teleport Paper";
            Location loc = player.getLocation();
            ItemStack tppaper = createLimitedTeleportPaper(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw(), paperName, locationName);

            player.getInventory().addItem(tppaper);
            player.sendMessage(ChatColor.GREEN + "Created a " + paperName + " at your current location.");

            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // No handling for teleportation, as per your requirements.
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // No handling for teleportation cancellation, as per your requirements.
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        // No handling for teleportation cancellation, as per your requirements.
    }

    private ItemStack createLimitedTeleportPaper(String worldName, double x, double y, double z, float pitch, float yaw, String displayName, String locationName) {
        ItemStack tppaper = new ItemStack(Material.PAPER);
        ItemMeta meta = tppaper.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + "Limited Use Teleport Paper",
                ChatColor.GRAY + "Right-click to teleport to " + locationName));
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(new NamespacedKey(plugin, "TELEPORT_PAPER"), PersistentDataType.STRING, String.join(",", worldName, Double.toString(x), Double.toString(y), Double.toString(z), Float.toString(pitch), Float.toString(yaw)));
            container.set(new NamespacedKey(plugin, "TELEPORT_PAPER_SWORD"), PersistentDataType.STRING, "true"); // Added this key
            container.set(new NamespacedKey(plugin, "LIMITED_TELEPORT_PAPER"), PersistentDataType.BYTE, (byte) 1);
            tppaper.setItemMeta(meta);
        }

        return tppaper;
    }
}
