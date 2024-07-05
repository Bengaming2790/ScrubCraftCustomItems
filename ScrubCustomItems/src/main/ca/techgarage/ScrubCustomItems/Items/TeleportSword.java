package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class TeleportSword implements CommandExecutor, Listener {

    private final Main plugin;

    public TeleportSword(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only Players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        ItemStack teleportSword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = teleportSword.getItemMeta();

        meta.setDisplayName(ChatColor.GRAY + "Teleport Sword");
        meta.setLore(Arrays.asList("When the sword kills a Hostile entity the entity",
                "will drop a teleport paper to",
                "the coordinates of its death"));

        meta.getPersistentDataContainer().set(Keys.TELEPORT_SWORD, PersistentDataType.BOOLEAN, true);

        teleportSword.setItemMeta(meta);
        player.getInventory().addItem(teleportSword);

        return true;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (entity instanceof Monster && killer != null) {
            ItemStack itemInHand = killer.getInventory().getItemInMainHand();

            // Check if the player is holding the teleport sword
            if (isTeleportSword(itemInHand)) {
                // Create the teleport paper
                ItemStack tppaper = createTeleportPaper(entity.getLocation());

                // Drop the teleport paper
                if (tppaper != null) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), tppaper);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Check if the paper is a teleport paper
            if (container.has(Keys.TELEPORT_PAPER, PersistentDataType.STRING)) {
                // Get the location from the paper's data
                String locationString = container.get(Keys.TELEPORT_PAPER, PersistentDataType.STRING);
                String[] parts = locationString.split(",");
                if (parts.length == 3) {
                    try {
                        double x = Double.parseDouble(parts[0]);
                        double y = Double.parseDouble(parts[1]);
                        double z = Double.parseDouble(parts[2]);

                        Location teleportLocation = new Location(player.getWorld(), x, y, z);

                        // Teleport the player
                        player.teleport(teleportLocation);
                        player.sendMessage(ChatColor.GREEN + "Teleported to " + x + ", " + y + ", " + z);

                        // Remove one teleport paper from the player's hand
                        item.setAmount(item.getAmount() - 1);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "Invalid teleport location.");
                    }
                }
            }
        }
    }

    private ItemStack createTeleportPaper(Location location) {
        ItemStack tppaper = new ItemStack(Material.PAPER);
        ItemMeta meta = tppaper.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.BLUE + "Teleport Paper");
            meta.setLore(Arrays.asList(
                    "Limited Use Teleport Paper",
                    ChatColor.GRAY + "Right click to teleport to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 10, true); // Example enchantment

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.TELEPORT_PAPER, PersistentDataType.STRING, location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());

            tppaper.setItemMeta(meta);
        }

        return tppaper;
    }

    private boolean isTeleportSword(ItemStack itemInHand) {
        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(Keys.TELEPORT_SWORD, PersistentDataType.BOOLEAN);
    }
}
