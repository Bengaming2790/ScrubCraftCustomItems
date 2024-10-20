package main.ca.techgarage.ScrubCustomItems.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
import main.ca.techgarage.ScrubCustomItems.LanguageManager;

public class TeleportPaperCreate implements CommandExecutor, Listener {

    private final Main plugin;
    private final LanguageManager lang; // Add LanguageManager reference
    private final Map<UUID, BukkitRunnable> pendingTeleports = new HashMap<>();

    public TeleportPaperCreate(Main plugin, LanguageManager lang) {
        this.plugin = plugin;
        this.lang = lang; // Initialize LanguageManager
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teleportpapercreate")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(lang.getMessage("teleport_paper_create_only_player", "This command can only be executed by a player."));
                return true;
            }

            Player player = (Player) sender;

            if (!player.hasPermission("sc.customitems.teleportpapercreate")) {
                player.sendMessage(lang.getMessage("teleport_paper_create_no_permission", "You do not have permission to execute this command."));
                return true;
            }

            String locationName = args.length > 0 ? args[0].replace('_', ' ') : lang.getMessage("default_location_name", "Teleport Location");
            String paperName = lang.getMessage("teleport_paper_name", "{location_name} Teleport Paper").replace("{location_name}", locationName);
            Location loc = player.getLocation();
            ItemStack tppaper = createTeleportPaper(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw(), paperName, true, locationName);

            player.getInventory().addItem(tppaper);
            player.sendMessage(lang.getMessage("teleport_paper_created", "Created a {paper_name} at your current location.").replace("{paper_name}", paperName));

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

            player.sendMessage(lang.getMessage("teleport_cancelled_inventory_open", "Teleport cancelled due to opening a container."));
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
            player.sendMessage(lang.getMessage("teleportation_cancelled", "Teleportation cancelled."));
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
                player.sendMessage(lang.getMessage("teleporting_in_5_seconds", "Teleporting in 5 seconds..."));

                BukkitRunnable teleportTask = new BukkitRunnable() {
                    public void run() {
                        player.teleport(teleportLocation);
                        player.removePotionEffect(PotionEffectType.NAUSEA);
                        player.removePotionEffect(PotionEffectType.DARKNESS);
                        player.sendMessage(lang.getMessage("teleported_successfully", "Teleported..."));

                        pendingTeleports.remove(player.getUniqueId());
                    }
                };

                pendingTeleports.put(player.getUniqueId(), teleportTask);
                teleportTask.runTaskLater(plugin, 100L);
            } catch (NumberFormatException e) {
                player.sendMessage(lang.getMessage("invalid_teleport_location", "Invalid teleport location."));
            }
        }
    }

    private ItemStack createTeleportPaper(String worldName, double x, double y, double z, float pitch, float yaw, String displayName, boolean isPermanent, String locationName) {
        ItemStack tppaper = new ItemStack(Material.PAPER);
        ItemMeta meta = tppaper.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(
                lang.getMessage("teleport_paper_lore_permanent", "{status} Teleport Paper").replace("{status}", lang.getMessage("permanent_status", "Permanent")),
                lang.getMessage("teleport_paper_lore_right_click", "Right-click to teleport to {location_name}").replace("{location_name}", locationName)
            ));
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.TELEPORT_PAPER, PersistentDataType.STRING, worldName + "," + x + "," + y + "," + z + "," + pitch + "," + yaw);

            tppaper.setItemMeta(meta);
        }

        return tppaper;
    }
}
