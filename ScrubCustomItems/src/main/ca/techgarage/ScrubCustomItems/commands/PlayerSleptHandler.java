package main.ca.techgarage.ScrubCustomItems.commands;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSpawnChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class PlayerSleptHandler implements Listener {

    private static final File FILE = new File("plugins/ScrubCustomItems/playersSleep.txt");
    private final Set<String> joinedPlayers = new HashSet<>();

    public PlayerSleptHandler() {
        try {
            if (!FILE.exists()) {
                // Create the file if it does not exist
                FILE.getParentFile().mkdirs(); // Create the directory if it doesn't exist
                FILE.createNewFile();
            }
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerSpawnChange(PlayerSpawnChangeEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (!joinedPlayers.contains(playerName)) {
            joinedPlayers.add(playerName);
            save();

            // Make the server execute the spreadplayers command for this player
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "give "+ playerName + " minecraft:paper[custom_name='[\"\",{\"text\":\"Spawn Teleport Paper\",\"italic\":false,\"color\":\"dark_purple\"}]',lore=['[\"\",{\"text\":\"Permanant Teleport Paper\",\"italic\":false}]','[\"\",{\"text\":\"Right-click to teleport to Spawn\",\"italic\":false,\"color\":\"gray\"}]'],item_name=paper,rarity=rare,enchantments={levels:{swift_sneak:10},show_in_tooltip:false}, minecraft:custom_data={PublicBukkitValues: {\"scrubcustomitems:teleportpaper\": \"world,-1421.239246288276,155.0,100.46981862907359\"}}] 1"   
            );
        }
    }


    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                joinedPlayers.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE))) {
            for (String playerName : joinedPlayers) {
                writer.write(playerName);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        save();
    }
}