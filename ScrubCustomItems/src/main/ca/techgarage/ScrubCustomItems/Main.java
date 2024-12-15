package main.ca.techgarage.ScrubCustomItems;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import main.ca.techgarage.ScrubCustomItems.Items.*;
import main.ca.techgarage.ScrubCustomItems.auras.*;
import main.ca.techgarage.ScrubCustomItems.commands.*;
import main.ca.techgarage.ScrubCustomItems.scythes.*;

public class Main extends JavaPlugin implements Listener {
    private BukkitTask task;
    private PlayerJoinHandler playerJoinHandler;
    private Drops drops;
    private LanguageManager languageManager;
    private TeleportPaperGUI teleportPaperGUI;

    @Override
    public void onEnable() {
        // Initialize TeleportPaperGUI
        teleportPaperGUI = new TeleportPaperGUI(this);

        getLogger().info("ScrubCustomItems plugin is enabling...");
        
        PluginCommand teleportCommand = getCommand("teleportpapergui");

        if (teleportCommand != null) {
            teleportCommand.setExecutor(new TeleportPaperGUI(this));
        } else {
            getLogger().warning("The teleportpapergui command is not registered in plugin.yml!");
        }

        getServer().getPluginManager().registerEvents(new TeleportPaperGUI(this), this);
    
        
        
        // Load the language manager
        int pluginId = 23958; // Replace with your plugin ID
        Metrics metrics = new Metrics(this, pluginId);
        this.saveDefaultConfig();
        
        // Now you can load or manipulate the data in the config
        // Register Commands
        registerCommands();

        // Register Events
        registerEvents();

        // Add Recipes
        addRecipes();

        // Drops manager
        drops = new Drops(this);

        // Initialize Player Join Handler
        playerJoinHandler = new PlayerJoinHandler();

        // Register additional commands and handlers
        this.getCommand("10k").setExecutor(new TeleportCommandExecutor());
        this.getCommand("candy").setExecutor(new Candy());
        this.getServer().getPluginManager().registerEvents(playerJoinHandler, this);
        this.getServer().getPluginManager().registerEvents(new PlayerSleptHandler(), this);
        this.getCommand("scythe").setExecutor(new Scythe());
        this.getCommand("headhunter").setExecutor(new HeadHunter());
        this.getCommand("freezeclock").setExecutor(new FreezeClock());
        this.getCommand("bubbleaura").setExecutor(new BubbleAura(this));
        this.getCommand("flameaura").setExecutor(new FlameAura(this));
        this.getCommand("heavyscythe").setExecutor(new Heavy());
        this.getCommand("swiftscythe").setExecutor(new Swift());
        this.getCommand("abyssalscythe").setExecutor(new Abyssal());
        this.getCommand("smilesscythe").setExecutor(new Smile());
        this.getCommand("iciclescythe").setExecutor(new Icicle());
        this.getCommand("shulkerscythe").setExecutor(new Shulker());
        this.getCommand("ghastscythe").setExecutor(new Ghast());
       // this.getCommand("flamecharge").setExecutor(new flameCharge(this)); 
        this.getCommand("questbook").setExecutor(new QuestBook());
        this.getCommand("breezescythe").setExecutor(new Breeze());
        this.getCommand("backpack").setExecutor(new BackPacks());
        this.getCommand("claimlamp").setExecutor(new ClaimLamp());
        this.getCommand("endrodaura").setExecutor(new EnderAura(this));
        this.getCommand("ashaura").setExecutor(new AshAura(this));
        this.getCommand("heartaura").setExecutor(new HeartAura(this));
        this.getCommand("spitaura").setExecutor(new SpitAura(this));
        this.getCommand("witchaura").setExecutor(new WitchAura(this));

        this.getServer().getPluginManager().registerEvents(playerJoinHandler, this);
        this.getServer().getPluginManager().registerEvents(new PlayerSleptHandler(), this);
        
        // Load the GUI inventory from the config
        teleportPaperGUI.loadInventoryFromConfig();

        getLogger().info("Scrub Custom Items has been enabled.");
    }

    @Override
    public void onDisable() {
        // Save GUI inventory to the config
        if (teleportPaperGUI != null) {
            teleportPaperGUI.saveInventoryToConfig();
        }

        // Shutdown player join handler
        if (playerJoinHandler != null) {
            playerJoinHandler.shutdown();
        }

        // Cancel scheduled tasks
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }

        getLogger().info("Scrub Custom Items has been disabled.");
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
            getCommand("teleportpapercreate").setExecutor(new TeleportPaperCreate(this, teleportPaperGUI));
        }
        if (getCommand("limitedteleportpapercreate") != null) {
            getCommand("limitedteleportpapercreate").setExecutor(new LimitedTeleportPaperCreate(this));
        }

    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new FlameAura(this), this);
        getServer().getPluginManager().registerEvents(new AshAura(this), this);
        getServer().getPluginManager().registerEvents(new broadcast(), this);
        getServer().getPluginManager().registerEvents(new Edgestone(), this);
        getServer().getPluginManager().registerEvents(new ServerHeart(), this);
        getServer().getPluginManager().registerEvents(new BloodDust(), this);
        getServer().getPluginManager().registerEvents(new TeleportSword(this), this);
        getServer().getPluginManager().registerEvents(new HeadHunter(), this);
        getServer().getPluginManager().registerEvents(new harmingstick(), this);
        getServer().getPluginManager().registerEvents(new QuestBook(), this);
        getServer().getPluginManager().registerEvents(new FreezeClock(), this);
        getServer().getPluginManager().registerEvents(new Breeze(), this);
        getServer().getPluginManager().registerEvents(new BackPacks(), this);
        getServer().getPluginManager().registerEvents(new ClaimLamp(), this);
        getServer().getPluginManager().registerEvents(new Heavy(), this);
        getServer().getPluginManager().registerEvents(new Swift(), this);
        getServer().getPluginManager().registerEvents(new Abyssal(), this);
        getServer().getPluginManager().registerEvents(new Smile(), this);
        getServer().getPluginManager().registerEvents(new Icicle(), this);
        getServer().getPluginManager().registerEvents(new Shulker(), this);
        getServer().getPluginManager().registerEvents(new Ghast(), this);
        getServer().getPluginManager().registerEvents(new EnderAura(this), this);
        getServer().getPluginManager().registerEvents(new BubbleAura(this), this);
        getServer().getPluginManager().registerEvents(new HeartAura(this), this);
        getServer().getPluginManager().registerEvents(new SpitAura(this), this);
        getServer().getPluginManager().registerEvents(new WitchAura(this), this);

        // Register TeleportPaperGUI as an event listener
    }

    private void addRecipes() {
        Bukkit.addRecipe(Edgestone.getEdgestoneRecipe(this));
        Scythe scythe = new Scythe();
        scythe.addScytheRecipe(this);
        Icicle icicle = new Icicle();
        icicle.addIScytheRecipe(this);
        Shulker shulker = new Shulker();
        shulker.addIScytheRecipe(this);
        Breeze breeze = new Breeze();
        breeze.addIScytheRecipe(this);
        Heavy heavy = new Heavy();
        heavy.addHScytheRecipe(this);
        Swift swift = new Swift();
        swift.addSScytheRecipe(this);
        Ghast ghast = new Ghast();
        ghast.addIScytheRecipe(this);
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }
}
