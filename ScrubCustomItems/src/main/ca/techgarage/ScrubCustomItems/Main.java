package main.ca.techgarage.ScrubCustomItems;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import main.ca.techgarage.ScrubCustomItems.Items.BackPacks;
import main.ca.techgarage.ScrubCustomItems.Items.BloodDust;
import main.ca.techgarage.ScrubCustomItems.Items.ClaimLamp;
import main.ca.techgarage.ScrubCustomItems.Items.Edgestone;
import main.ca.techgarage.ScrubCustomItems.Items.FreezeClock;
import main.ca.techgarage.ScrubCustomItems.Items.HeadHunter;
import main.ca.techgarage.ScrubCustomItems.Items.QuestBook;
import main.ca.techgarage.ScrubCustomItems.Items.Scythe;
import main.ca.techgarage.ScrubCustomItems.Items.ServerHeart;
import main.ca.techgarage.ScrubCustomItems.Items.TeleportSword;
import main.ca.techgarage.ScrubCustomItems.Items.flameCharge;
import main.ca.techgarage.ScrubCustomItems.Items.harmingstick;
import main.ca.techgarage.ScrubCustomItems.auras.EnderAura;
import main.ca.techgarage.ScrubCustomItems.auras.FlameAura;
import main.ca.techgarage.ScrubCustomItems.auras.HeartAura;
import main.ca.techgarage.ScrubCustomItems.auras.SpitAura;
import main.ca.techgarage.ScrubCustomItems.auras.WitchAura;
import main.ca.techgarage.ScrubCustomItems.auras.AshAura;
import main.ca.techgarage.ScrubCustomItems.auras.BubbleAura;
import main.ca.techgarage.ScrubCustomItems.commands.LimitedTeleportPaperCreate;
import main.ca.techgarage.ScrubCustomItems.commands.PlayerJoinHandler;
import main.ca.techgarage.ScrubCustomItems.commands.PlayerSleptHandler;
import main.ca.techgarage.ScrubCustomItems.commands.TeleportCommandExecutor;
import main.ca.techgarage.ScrubCustomItems.commands.TeleportPaperCreate;
import main.ca.techgarage.ScrubCustomItems.scythes.Abyssal;
import main.ca.techgarage.ScrubCustomItems.scythes.Breeze;
import main.ca.techgarage.ScrubCustomItems.scythes.Ghast;
import main.ca.techgarage.ScrubCustomItems.scythes.Heavy;
import main.ca.techgarage.ScrubCustomItems.scythes.Icicle;
import main.ca.techgarage.ScrubCustomItems.scythes.Shulker;
import main.ca.techgarage.ScrubCustomItems.scythes.Smile;
import main.ca.techgarage.ScrubCustomItems.scythes.Swift;
import main.ca.techgarage.ScrubCustomItems.Items.Candy;
public class Main extends JavaPlugin implements Listener{
    private BukkitTask task;
    private PlayerJoinHandler playerJoinHandler;
    private Drops drops;
    private LanguageManager languageManager;




    @Override
    public void onEnable() {
        // Load the language manager

    	int pluginId = 23958; // <-- Replace with the id of your plugin!
        Metrics metrics = new Metrics(this, pluginId);
        registerCommands();
        registerEvents();
        addRecipes();
        drops = new Drops(this);

        playerJoinHandler = new PlayerJoinHandler();
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

        
        
        getLogger().info("Scrub Custom Items has been enabled and drops are running.");
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
        //getServer().getPluginManager().registerEvents(new flameCharge(this), this);

    	
        getServer().getPluginManager().registerEvents(new FlameAura(this), this);
        getServer().getPluginManager().registerEvents(new AshAura(this), this);
        getServer().getPluginManager().registerEvents(new broadcast(), this);
        getServer().getPluginManager().registerEvents(new Edgestone(), this);
        getServer().getPluginManager().registerEvents(new ServerHeart(), this);
        getServer().getPluginManager().registerEvents(new BloodDust(), this);
        getServer().getPluginManager().registerEvents(new TeleportSword(this), this);
        getServer().getPluginManager().registerEvents(new HeadHunter(), this);
        getServer().getPluginManager().registerEvents(new harmingstick(), this);
        getServer().getPluginManager().registerEvents(new TeleportPaperCreate(this), this);
        getServer().getPluginManager().registerEvents(new LimitedTeleportPaperCreate(this), this);
        //getServer().getPluginManager().registerEvents(new Candy(), this);

        getServer().getPluginManager().registerEvents(new QuestBook(), this);
        getServer().getPluginManager().registerEvents(new FreezeClock(), this);
        getServer().getPluginManager().registerEvents(new Breeze(), this);
        getServer().getPluginManager().registerEvents(new BackPacks(), this);
        getServer().getPluginManager().registerEvents(new ClaimLamp(), this);
        getServer().getPluginManager().registerEvents(new Swift(), this);

        getServer().getPluginManager().registerEvents(new Heavy(), this);
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

    }

    private void addRecipes() {
        // Register existing recipes
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



        // Register new recipes
    }

    public static Main getInstance() {
        return getPlugin(Main.class);
    }

}
