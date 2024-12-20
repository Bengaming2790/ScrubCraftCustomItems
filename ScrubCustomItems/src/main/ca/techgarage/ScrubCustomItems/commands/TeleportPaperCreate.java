package main.ca.techgarage.ScrubCustomItems.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class TeleportPaperCreate implements CommandExecutor, Listener {

    private final Main plugin;
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();

    
    private final TeleportPaperGUI teleportPaperGUI;

    public TeleportPaperCreate(Main plugin, TeleportPaperGUI gui) {
        this.plugin = plugin;
        this.teleportPaperGUI = gui;
    }

    // After creating a teleport paper
   


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportpapercreate")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("sc.customitems.teleportpapercreate")) {
                player.sendMessage(ChatColor.RED + "You do not have permission to execute this command.");
                return true;
            }
            String locationName = args.length > 0 ? args[0].replace('_', ' ') : "Teleport Location";
            String paperName = ChatColor.DARK_PURPLE + locationName + " Teleport Paper";
            Location loc = player.getLocation();
            ItemStack tppaper = createTeleportPaper(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw(), paperName, true, locationName);

            player.getInventory().addItem(tppaper);
            player.sendMessage(ChatColor.GREEN + "Created a " + paperName + " at your current location.");

            return true;
        }

        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            if (container.has(Keys.TELEPORT_PAPER, PersistentDataType.STRING)) {
                handleTeleport(player, container, item);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (isTeleportPaper(item)) {
            cancelTeleport(player);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        // Check if the player has a pending teleport
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            // Cancel the pending teleport
            BukkitRunnable teleportTask = pendingTeleports.get(player.getUniqueId());
            teleportTask.cancel();
            pendingTeleports.remove(player.getUniqueId());

            player.removePotionEffect(PotionEffectType.NAUSEA);
            player.removePotionEffect(PotionEffectType.DARKNESS);

            player.sendMessage(ChatColor.RED + "Teleport cancelled due to opening a container.");
        }
    }

    private boolean isTeleportPaper(ItemStack item) {
        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            return container.has(Keys.TELEPORT_PAPER, PersistentDataType.STRING);
        }
        return false;
    }

    private void cancelTeleport(Player player) {
        if (pendingTeleports.containsKey(player.getUniqueId())) {
            pendingTeleports.get(player.getUniqueId()).cancel();
            pendingTeleports.remove(player.getUniqueId());
            player.removePotionEffect(PotionEffectType.NAUSEA);
            player.removePotionEffect(PotionEffectType.DARKNESS);
            player.sendMessage(ChatColor.RED + "Teleportation cancelled.");
        }
    }

    private void handleTeleport(Player player, PersistentDataContainer container, ItemStack item) {
        cancelTeleport(player);

        String locationString = container.get(Keys.TELEPORT_PAPER, PersistentDataType.STRING);
        String[] parts = locationString.split(",");
        if (parts.length == 6) {
            try {
                String worldName = parts[0];
                double tpX = Double.parseDouble(parts[1]);
                double tpY = Double.parseDouble(parts[2]);
                double tpZ = Double.parseDouble(parts[3]);
                float tpPitch = Float.parseFloat(parts[4]);
                float tpYaw = Float.parseFloat(parts[5]);

                Location teleportLocation = new Location(plugin.getServer().getWorld(worldName), tpX, tpY, tpZ, tpYaw, tpPitch);
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 160, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 160, 1));
                player.sendMessage(ChatColor.GREEN + "Teleporting in 5 seconds...");

                BukkitRunnable teleportTask = new BukkitRunnable() {
                    public void run() {
                        player.teleport(teleportLocation);
                        player.removePotionEffect(PotionEffectType.NAUSEA);
                        player.removePotionEffect(PotionEffectType.DARKNESS);
                        player.sendMessage(ChatColor.GREEN + "Teleported...");

                        pendingTeleports.remove(player.getUniqueId());
                    }
                };

                pendingTeleports.put(player.getUniqueId(), teleportTask);
                teleportTask.runTaskLater(plugin, 100L);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid teleport location.");
            }
        }
    }

    private ItemStack createTeleportPaper(String worldName, double x, double y, double z, float pitch, float yaw, String displayName, boolean isPermanent, String locationName) {
        ItemStack tppaper = new ItemStack(Material.PAPER);
        ItemMeta meta = tppaper.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(
                ChatColor.DARK_PURPLE + (isPermanent ? "Permanent" : "Limited Use") + " Teleport Paper",
                ChatColor.GRAY + "Right-click to teleport to " + locationName));
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.TELEPORT_PAPER, PersistentDataType.STRING, worldName + "," + x + "," + y + "," + z + "," + pitch + "," + yaw);

            tppaper.setItemMeta(meta);
            
        }

        return tppaper;
        
    }
}