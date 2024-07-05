package main.ca.techgarage.ScrubCustomItems;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import main.ca.techgarage.ScrubCustomItems.Keys;
import main.ca.techgarage.ScrubCustomItems.Items.BloodDust;
import main.ca.techgarage.ScrubCustomItems.Items.Edgestone;
import main.ca.techgarage.ScrubCustomItems.Items.ServerHeart;
import main.ca.techgarage.ScrubCustomItems.Items.TeleportSword;
import main.ca.techgarage.ScrubCustomItems.commands.CustomItemCommands;
import main.ca.techgarage.ScrubCustomItems.commands.SetSpawnPointCommand;
import net.md_5.bungee.api.ChatColor;


@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener{

	private BukkitTask task;
    private Location spawnPoint;
    private NamespacedKey spawnPointKey;

	@Override
	public void onEnable() {
        spawnPointKey = new NamespacedKey(this, "spawnPoint");
        CustomItemCommands customItemCommands = new CustomItemCommands(this);
        
        SetSpawnPointCommand setSpawnPointCommand = new SetSpawnPointCommand(this);
        Edgestone edgestone = new Edgestone();
        ServerHeart serverheart = new ServerHeart();
        BloodDust blooddust = new BloodDust();

		System.out.println("Regestering Events for SCCI");
        getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		getServer().getPluginManager().registerEvents(new TeleportSword(this), this);
		getServer().getPluginManager().registerEvents(new Edgestone(), this);
		getServer().getPluginManager().registerEvents(new BloodDust(), this);
		getServer().getPluginManager().registerEvents(new ServerHeart(), this);
        getServer().getPluginManager().registerEvents(customItemCommands, this);
		System.out.println("Finished regestering Events for SCCI");
        getCommand("spawnpaper").setExecutor(customItemCommands);
		getCommand("edgestone").setExecutor(new Edgestone());
		getCommand("serverheart").setExecutor(new ServerHeart());
		getCommand("blooddust").setExecutor(new BloodDust());
		getCommand("teleportsword").setExecutor(new TeleportSword(this));
        getCommand("setSpawnPoint").setExecutor(setSpawnPointCommand);


      

	}

    public NamespacedKey getSpawnPointKey() {
        return spawnPointKey;
    }
	 
	
      
	@Override
	public void onDisable() {
		if (task != null && !task.isCancelled())
			task.cancel();
		
	}
	
	public static Main getInstance() {
		return getPlugin(Main.class);
	}

	  @EventHandler
	  public void onPlayerMove(PlayerMoveEvent event) {
	       Player player = event.getPlayer();

	        // Check if the block the player is standing on is stone
	        if (player.getLocation().getBlock().getType() == Material.STONE) {
	            player.sendMessage("You are walking on stone!");
	        }
	    }
	 
}
	
