package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Main;

public class TeleportSword implements CommandExecutor, Listener {

    private final Main plugin;
    private final Map<UUID, BukkitRunnable> pendingSwordTeleports = new HashMap<>();

    public TeleportSword(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /<command> <player name>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        ItemStack teleportSword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta meta = teleportSword.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Teleport Sword");
            meta.setLore(Arrays.asList("When the sword kills a Hostile ",
                    "entity the entity will drop a teleport",
                    " paper to the coordinates of its death",
                    "Teleport sword will break after 10 uses!"));

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.TELEPORT_SWORD, PersistentDataType.BOOLEAN, true);
            container.set(Keys.TELEPORT_SWORD_KILL_COUNT, PersistentDataType.INTEGER, 0); // Set initial kill count to 0

            teleportSword.setItemMeta(meta);
        }

        target.getInventory().addItem(teleportSword);
        sender.sendMessage(ChatColor.GREEN + "Gave a Teleport Sword to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received a Teleport Sword.");

        return true;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (isTeleportSword(itemInHand)) {
                event.getEntity().setMetadata("lastDamager", new FixedMetadataValue(plugin, player.getUniqueId()));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (entity instanceof Monster && killer != null) {
            if (!entity.hasMetadata("lastDamager")) {
                return;
            }

            MetadataValue metadata = entity.getMetadata("lastDamager").get(0);
            if (metadata.value() instanceof UUID) {
                UUID lastDamagerUUID = (UUID) metadata.value();
                if (killer.getUniqueId().equals(lastDamagerUUID)) {
                    ItemStack itemInHand = killer.getInventory().getItemInMainHand();

                    // Check if the player is holding the teleport sword
                    if (isTeleportSword(itemInHand) && killer.hasPermission("sc.teleportsword")) {
                        // Update the kill count
                        ItemMeta meta = itemInHand.getItemMeta();
                        PersistentDataContainer container = meta.getPersistentDataContainer();
                        int killCount = container.getOrDefault(Keys.TELEPORT_SWORD_KILL_COUNT, PersistentDataType.INTEGER, 0); // Fix the initial kill count to 0
                        killCount++;
                        container.set(Keys.TELEPORT_SWORD_KILL_COUNT, PersistentDataType.INTEGER, killCount);

                        // Set the updated meta back to the item
                        itemInHand.setItemMeta(meta);

                        // Check if the sword should break
                        if (killCount > 10) {
                            killer.sendMessage(ChatColor.RED + "Your Teleport Sword has broken!");
                            killer.getInventory().remove(itemInHand);
                            killer.playSound(killer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                        } else {
                            // Create the teleport paper
                            ItemStack tppaper = createTeleportPaper(entity.getLocation());

                            // Drop the teleport paper
                            if (tppaper != null) {
                                entity.getWorld().dropItemNaturally(entity.getLocation(), tppaper);
                            }
                        }
                    }
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
            if (container.has(Keys.TELEPORT_PAPER_SWORD, PersistentDataType.STRING)) {
                handleTeleport(player, container, item, pendingSwordTeleports);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (item != null && item.getType() == Material.PAPER && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();

            // Check if the paper is a teleport paper
            if (container.has(Keys.TELEPORT_PAPER_SWORD, PersistentDataType.STRING)) {
                cancelTeleport(player, pendingSwordTeleports);
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        cancelTeleport(player, pendingSwordTeleports);
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
            meta.addEnchant(Enchantment.SWIFT_SNEAK, 9, true); // Example enchantment

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.TELEPORT_PAPER_SWORD, PersistentDataType.STRING, location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());

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

    private void handleTeleport(Player player, PersistentDataContainer container, ItemStack item, Map<UUID, BukkitRunnable> pendingTeleports) {
        cancelTeleport(player, pendingTeleports);

        String locationString = container.get(Keys.TELEPORT_PAPER_SWORD, PersistentDataType.STRING);
        String[] parts = locationString.split(",");
        if (parts.length == 4) {
            try {
                String worldName = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);

                Location teleportLocation = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 160, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 160, 1));

                BukkitRunnable teleportTask = new BukkitRunnable() {
                    public void run() {
                        // Teleport the player
                        player.teleport(teleportLocation);
                        player.removePotionEffect(PotionEffectType.NAUSEA);
                        player.removePotionEffect(PotionEffectType.DARKNESS);

                        // Remove one teleport paper from the player's hand
                        if (item != null && item.getType() == Material.PAPER && hasEnchantment(item, Enchantment.SWIFT_SNEAK, 9)) {
                            item.setAmount(item.getAmount() - 1);
                        }

                        // Play the teleport sound
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

                        // Remove the player from the pending teleports map
                        pendingTeleports.remove(player.getUniqueId());
                    }
                };

                // Add the teleport task to the pending teleports map
                pendingTeleports.put(player.getUniqueId(), teleportTask);
                teleportTask.runTaskLater(plugin, 100L);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Invalid teleport location.");
            }
        }
    }


    private boolean hasEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.hasEnchant(enchantment)) {
            int enchantmentLevel = meta.getEnchantLevel(enchantment);
            return enchantmentLevel == level;
        }
        return false;
    }

    private void cancelTeleport(Player player, Map<UUID, BukkitRunnable> pendingTeleports) {
        UUID playerUUID = player.getUniqueId();
        if (pendingTeleports.containsKey(playerUUID)) {
            BukkitRunnable pendingTask = pendingTeleports.get(playerUUID);
            pendingTask.cancel();
            pendingTeleports.remove(playerUUID);
        }
    }
}
