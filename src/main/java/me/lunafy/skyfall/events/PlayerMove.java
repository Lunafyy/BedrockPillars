package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
    private final Skyfall plugin;

    public PlayerMove(Skyfall plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();

        if(plugin.getGameManager().getState() != GameState.STARTING) return;
        if(!plugin.getPlayerManager().hasPlayer(player)) return;

        // Allow players to turn their heads, but locking walking
        if(!event.getFrom().toVector().equals(event.getTo().toVector()))
        {
            event.setCancelled(true);
        }
    }
}
