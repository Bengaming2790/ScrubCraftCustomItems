package main.ca.techgarage.ScrubCustomItems.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import org.bukkit.entity.Player;

import main.ca.techgarage.ScrubCustomItems.Keys;

public class QuestBook implements CommandExecutor, Listener {

    // Create the quest book
    public ItemStack createQuestBook() {
        ItemStack qb = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = qb.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Quest Book");
            meta.getPersistentDataContainer().set(Keys.QUEST_BOOK_KEY, PersistentDataType.BOOLEAN, true); // Correct key

            qb.setItemMeta(meta);
        }
        
        return qb;
    }

    // Handle player right-click event
    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.ENCHANTED_BOOK) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.getPersistentDataContainer().has(Keys.QUEST_BOOK_KEY, PersistentDataType.BOOLEAN)) {
                // Run the command '/quests journal' for the player
                player.performCommand("quests journal");
                event.setCancelled(true); // Cancel any further action for the event
            }
        }
    }

    // Command handler (optional)
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

        ItemStack qb = createQuestBook();
        target.getInventory().addItem(qb);
        sender.sendMessage(ChatColor.GREEN + "Gave the Quest Book to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received the Quest Book.");
        return true;
    }
}
