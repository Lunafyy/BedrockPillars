package me.lunafy.skyfall;

import me.lunafy.skyfall.arena.ArenaManager;
import me.lunafy.skyfall.commands.Join;
import me.lunafy.skyfall.commands.StartGame;
import me.lunafy.skyfall.events.PlayerConnect;
import me.lunafy.skyfall.events.PlayerDamage;
import me.lunafy.skyfall.events.PlayerDisconnect;
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
        getServer().getPluginManager().registerEvents(new PlayerDamage(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDisconnect(this), this);
        getServer().getPluginManager().registerEvents(new PlayerConnect(this), this);

        // Register Commands
        getCommand("start").setExecutor(new StartGame(this));
        getCommand("join").setExecutor(new Join(this));

        Bukkit.getLogger().info("Online and working!");
    }

    @Override
    public void onDisable() {
        gameManager.cleanup();
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
