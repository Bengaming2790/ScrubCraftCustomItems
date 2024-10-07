package main.ca.techgarage.ScrubCustomItems;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Random;

public class Drops {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public Drops(JavaPlugin plugin) {
        this.plugin = plugin;
        startDropTask();
    }

    private void startDropTask() {
        // Schedule a repeating task every 30-60 minutes (in ticks: 20 ticks = 1 second, 36000 ticks = 30 minutes)
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Check if player has permission "sc.drops"
                    if (player.hasPermission("sc.drops")) {
                        giveRandomDrops(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, getRandomInterval());  // Run instantly, then repeat at random intervals
    }

    private long getRandomInterval() {
        // Get a random interval between 30 minutes (36000 ticks) and 60 minutes (72000 ticks)
        return 36000L + random.nextInt(36000);
    }

    private void giveRandomDrops(Player player) {
        String playerName = player.getName();

        // Roll for Blood Dust (60% chance)
        if (random.nextDouble() <= 0.6) {
            int bloodDustAmount = random.nextInt(6) + 1;  // 1-6 Blood Dust
            // Run the command: /blooddust playername bloodDustAmount
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "blooddust " + playerName + " " + bloodDustAmount);
        }

        // Roll for Server Hearts (35% chance)
        if (random.nextDouble() <= 0.35) {
            int serverHeartsAmount = random.nextInt(2) + 1;  // 1-2 Server Hearts
            // Run the command: /serverhearts playername serverHeartsAmount
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "serverheart " + playerName + " " + serverHeartsAmount);
        }

        // Roll for Edgestone (5% chance)
        if (random.nextDouble() <= 0.045) {
            // Run the command: /edgestone playername 1 (since only 1 Edgestone is given)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "edgestone " + playerName + " 1");
        }
        if (random.nextDouble() <= 0.005) {
            // Run the command: /edgestone playername 1 (since only 1 Edgestone is given)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "edgestone " + playerName + " 4");
        }
    }
}
