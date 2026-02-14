package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDisconnect implements Listener {
    private final GameManager gameManager;
    private final PlayerManager playerManager;

    public PlayerDisconnect(Skyfall plugin)
    {
        this.gameManager = plugin.getGameManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {
        Player bukkitPlayer = event.getPlayer();
        SkyfallPlayer player = playerManager.getPlayer(bukkitPlayer);

        if (player == null) return;
        if (!player.isAlive()) return;

        playerManager.removePlayer(bukkitPlayer); // Remove them entirely, this allows them to queue into a different game

        if (gameManager.getState() != GameState.IN_GAME && gameManager.getState() != GameState.STARTING) return;

        playerManager.announceMessage(StringHelpers.format(
                "<yellow>" + bukkitPlayer.getName() + "</yellow>" +
                        " <red> has been eliminated</red>" +
                        " <gray>(" + event.getReason() +")</gray>"
        ));

        gameManager.checkWinConditions();
    }
}
