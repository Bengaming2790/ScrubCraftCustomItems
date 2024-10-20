package main.ca.techgarage.ScrubCustomItems;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LanguageManager {
    private final Main plugin; // Your main plugin instance
    private FileConfiguration langConfig;

    public LanguageManager(Main plugin) {
        this.plugin = plugin;
        loadLanguageFile();
    }

    public void loadLanguageFile() {
        // Check if the resource exists in the specified package
        InputStream resourceStream = plugin.getClass().getResourceAsStream("/resources/messages_en.yml");
        
        if (resourceStream == null) {
            // Handle the case where the resource is not found
            plugin.getLogger().severe("Could not find messages_en.yml in the resources!");
            return; // Exit the method if the resource is missing
        }

        // Create a temp file to hold the resource
        File tempFile = new File(plugin.getDataFolder(), "messages_en.yml");

        try (InputStreamReader reader = new InputStreamReader(resourceStream, StandardCharsets.UTF_8)) {
            // Save the resource to the temp file
            plugin.saveResource("messages_en.yml", false);
            langConfig = YamlConfiguration.loadConfiguration(tempFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load the language file: " + e.getMessage());
        }
    }

    public String getMessage(String key, String defaultValue) {
        if (langConfig == null) {
            return "Language configuration not loaded."; // Handle error case
        }

        // Retrieve the message using the key
        String message = langConfig.getString(key);

        if (message == null) {
            plugin.getLogger().warning("Missing message for key: " + key);
            return defaultValue; // Return the default value if the key is missing
        }

        return message; // Return the retrieved message
    }
}
