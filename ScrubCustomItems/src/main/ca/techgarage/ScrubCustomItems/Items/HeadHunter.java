package main.ca.techgarage.ScrubCustomItems.Items;

import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import main.ca.techgarage.ScrubCustomItems.Keys;

public class HeadHunter implements CommandExecutor, Listener {
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

        ItemStack headHunter = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = headHunter.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GRAY + "Head Hunter");
            meta.setLore(Arrays.asList("Drops the head of", "the killed entity"));

            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(Keys.HEAD_HUNTER_SWORD, PersistentDataType.BOOLEAN, true);

            headHunter.setItemMeta(meta);
        }

        target.getInventory().addItem(headHunter);
        sender.sendMessage(ChatColor.GREEN + "Gave a Head Hunter to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Head Hunter.");

        return true;
    }

    private boolean isHeadHunter(ItemStack itemInHand) {
        if (itemInHand == null || !itemInHand.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = itemInHand.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        return container.has(Keys.HEAD_HUNTER_SWORD, PersistentDataType.BOOLEAN);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            ItemStack itemInHand = killer.getInventory().getItemInMainHand();

            if (isHeadHunter(itemInHand)) {
                ItemStack head = getEntityHead(entity);

                if (head != null) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), head);
                }
            }
        }
    }

    private ItemStack getEntityHead(Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

            if (meta != null) {
                meta.setOwningPlayer(player);
                playerHead.setItemMeta(meta);
            }

            return playerHead;
        } else {
            EntityType type = entity.getType();
            Material headMaterial = getHeadMaterial(type);

            if (headMaterial != null) {
                return new ItemStack(headMaterial);
            }
        }

        return null;
    }

    private Material getHeadMaterial(EntityType type) {
        switch (type) {
            case ZOMBIE:
                return Material.ZOMBIE_HEAD;
            case SKELETON:
                return Material.SKELETON_SKULL;
            case PIGLIN:
                return Material.PIGLIN_HEAD;
            case CREEPER:
                return Material.CREEPER_HEAD;
            case WITHER_SKELETON:
                return Material.WITHER_SKELETON_SKULL;
            case ENDER_DRAGON:
                return Material.DRAGON_HEAD;
            // Add more cases for other entities as needed
            default:
                return Material.PLAYER_HEAD;
        }
    }
}
