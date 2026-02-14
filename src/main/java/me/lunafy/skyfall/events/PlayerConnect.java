package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.arena.ArenaManager;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerConnect implements Listener {
    private final ArenaManager arenaManager;

    public PlayerConnect(Skyfall plugin)
    {
        this.arenaManager = plugin.getArenaManager();
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event)
    {
        System.out.println("Login called");
        World defaultWorld = arenaManager.getDefaultWorld();
        Player player = event.getPlayer();

        // Reset the player
        player.teleport(defaultWorld.getSpawnLocation());
        player.clearActivePotionEffects();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
    }
}
