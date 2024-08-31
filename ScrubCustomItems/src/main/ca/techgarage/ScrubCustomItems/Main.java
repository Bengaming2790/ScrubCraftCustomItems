package main.ca.techgarage.ScrubCustomItems;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import main.ca.techgarage.ScrubCustomItems.Items.BloodDust;
import main.ca.techgarage.ScrubCustomItems.Items.Edgestone;
import main.ca.techgarage.ScrubCustomItems.Items.HeadHunter;
import main.ca.techgarage.ScrubCustomItems.Items.Scythe;
import main.ca.techgarage.ScrubCustomItems.Items.ServerHeart;
import main.ca.techgarage.ScrubCustomItems.Items.TeleportSword;
import main.ca.techgarage.ScrubCustomItems.Items.harmingstick;
import main.ca.techgarage.ScrubCustomItems.commands.LimitedTeleportPaperCreate;
import main.ca.techgarage.ScrubCustomItems.commands.PlayerJoinHandler;
import main.ca.techgarage.ScrubCustomItems.commands.TeleportCommandExecutor;
import main.ca.techgarage.ScrubCustomItems.commands.TeleportPaperCreate;
import main.ca.techgarage.ScrubCustomItems.scyths.Abyssal;
import main.ca.techgarage.ScrubCustomItems.scyths.Icicle;
import main.ca.techgarage.ScrubCustomItems.scyths.Smile;

public class Main extends JavaPlugin {
    private BukkitTask task;
    private PlayerJoinHandler playerJoinHandler;

    @Override
    public void onEnable() {
        registerCommands();
        registerEvents();
        addRecipes();
        
        playerJoinHandler = new PlayerJoinHandler();
        this.getCommand("10k").setExecutor(new TeleportCommandExecutor());
        this.getServer().getPluginManager().registerEvents(playerJoinHandler, this);
        this.getCommand("scythe").setExecutor(new Scythe());

        this.getCommand("abyssalscythe").setExecutor(new Abyssal());
        this.getCommand("smilesscythe").setExecutor(new Smile());
        this.getCommand("iciclescythe").setExecutor(new Icicle());
    }
          
    @Override
    public void onDisable() {
        if (playerJoinHandler != null) {
            playerJoinHandler.shutdown();
        }
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }

    private void registerCommands() {
        if (getCommand("edgestone") != null) {
            getCommand("edgestone").setExecutor(new Edgestone());
        }
        if (getCommand("serverheart") != null) {
            getCommand("serverheart").setExecutor(new ServerHeart());
        }
        if (getCommand("blooddust") != null) {
            getCommand("blooddust").setExecutor(new BloodDust());
        }
        if (getCommand("teleportsword") != null) {
            getCommand("teleportsword").setExecutor(new TeleportSword(this));
        }
        if (getCommand("teleportpapercreate") != null) {
            getCommand("teleportpapercreate").setExecutor(new TeleportPaperCreate(this));
        }
        if (getCommand("limitedteleportpapercreate") != null) {
            getCommand("limitedteleportpapercreate").setExecutor(new LimitedTeleportPaperCreate(this));
        }
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new Edgestone(), this);
        getServer().getPluginManager().registerEvents(new ServerHeart(), this);
        getServer().getPluginManager().registerEvents(new BloodDust(), this);
        getServer().getPluginManager().registerEvents(new TeleportSword(this), this);
        getServer().getPluginManager().registerEvents(new HeadHunter(), this);
        getServer().getPluginManager().registerEvents(new harmingstick(), this);
        getServer().getPluginManager().registerEvents(new TeleportPaperCreate(this), this);
        
        getServer().getPluginManager().registerEvents(new Abyssal(), this);
        getServer().getPluginManager().registerEvents(new Smile(), this);
        getServer().getPluginManager().registerEvents(new Icicle(), this);
    }

    private void addRecipes() {
        // Register existing recipes
        Bukkit.addRecipe(Edgestone.getEdgestoneRecipe(this));
        Scythe scythe = new Scythe();
        scythe.addScytheRecipe(this);
        Icicle icicle = new Icicle();
        icicle.addIScytheRecipe(this); 
        // Register new recipes
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }
}
