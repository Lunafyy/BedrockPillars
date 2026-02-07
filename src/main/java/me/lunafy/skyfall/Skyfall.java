package me.lunafy.skyfall;

import me.lunafy.skyfall.arena.ArenaManager;
import me.lunafy.skyfall.commands.GenerateWorld;
import me.lunafy.skyfall.commands.Join;
import me.lunafy.skyfall.commands.StartGame;
import me.lunafy.skyfall.events.PlayerDeath;
import me.lunafy.skyfall.events.PlayerMove;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Skyfall extends JavaPlugin {
    private GameManager gameManager;
    private ArenaManager arenaManager;
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        // Instanciate Managers
        arenaManager = new ArenaManager();
        playerManager = new PlayerManager();
        gameManager = new GameManager(this);

        // Register Events
        getServer().getPluginManager().registerEvents(new PlayerMove(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeath(this), this);

        // Register Commands
        getCommand("generate").setExecutor(new GenerateWorld(this));
        getCommand("start").setExecutor(new StartGame(this));
        getCommand("join").setExecutor(new Join(this));

        Bukkit.getLogger().info("Online and working!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaManager getArenaManager()
    {
        return arenaManager;
    }
    public GameManager getGameManager()
    {
        return gameManager;
    }
    public PlayerManager getPlayerManager()
    {
        return playerManager;
    }
}
