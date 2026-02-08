package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeath implements Listener {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    public PlayerDeath(Skyfall plugin)
    {
        this.playerManager = plugin.getPlayerManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player bukkitPlayer = event.getPlayer();
        SkyfallPlayer player = playerManager.getPlayer(bukkitPlayer);

        if (player == null) return;
        if (!player.isAlive()) return;
        if (gameManager.getState() != GameState.IN_GAME) return;

        event.deathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);

        // Update their SkyfallPlayer object
        player.setAlive(false);

        bukkitPlayer.spigot().respawn();
        bukkitPlayer.setGameMode(GameMode.SPECTATOR);

        Player killer = bukkitPlayer.getKiller();

        if (killer != null) {
            playerManager.announceMessage(StringHelpers.format(
                    "<red>" + bukkitPlayer.getName() +
                            " has been eliminated by <yellow>" + killer.getName() + "</yellow></red>"
            ));
        } else {
            playerManager.announceMessage(StringHelpers.format(
                    "<red>" + bukkitPlayer.getName() + " has been eliminated</red>"
            ));
        }

        // Call win check
        gameManager.checkWinConditions();
    }

}
