package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageEvent implements Listener {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    public PlayerDamageEvent(Skyfall plugin)
    {
        this.playerManager = plugin.getPlayerManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e)
    {
        if(gameManager.getState() != GameState.IN_GAME) return; // Don't look for deaths if a game isn't active
        if(!(e.getEntity() instanceof Player player)) return; // If it's some mob, ignore it
        SkyfallPlayer sfPlayer = playerManager.getPlayer(player);

        if(sfPlayer == null) return; // If they're not in the game, ignore it
        if(!sfPlayer.isAlive()) return; // If we already consider them dead, ignore it
        if(e.getCause() != EntityDamageEvent.DamageCause.VOID) return; // If it wasn't to the void, ignore it

        sfPlayer.setAlive(false);

        player.getInventory().clear();

        playerManager.announceMessage(StringHelpers.format(
                "<red>" + player.getName() + " has been eliminated</red>"
        ));
    }
}
