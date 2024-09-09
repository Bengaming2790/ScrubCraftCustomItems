package main.ca.techgarage.ScrubCustomItems.commands;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class PlayerJoinHandler implements Listener {

    private static final File FILE = new File("plugins/ScrubCustomItems/players.txt");
    private final Set<String> joinedPlayers = new HashSet<>();

    public PlayerJoinHandler() {
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
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (!joinedPlayers.contains(playerName)) {
            joinedPlayers.add(playerName);
            save();
            player.performCommand("10k");
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