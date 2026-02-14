package me.lunafy.skyfall.events;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamage implements Listener {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    public PlayerDamage(Skyfall plugin)
    {
        this.playerManager = plugin.getPlayerManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e)
    {
        if(gameManager.getState() != GameState.IN_GAME && gameManager.getState() != GameState.STARTING) return; // Don't look for deaths if a game isn't active
        if(!(e.getEntity() instanceof Player player)) return; // If it's some mob, ignore it

        SkyfallPlayer sfPlayer = playerManager.getPlayer(player);
        if(sfPlayer == null || !sfPlayer.isAlive()) return;

        double finalHealth = player.getHealth() - e.getFinalDamage();
        boolean voidDamage = e.getCause() == EntityDamageEvent.DamageCause.VOID;
        boolean lethalPlayerDamage = finalHealth <= 0;

        if(!voidDamage && !lethalPlayerDamage) return; // Cancel player kills for our own logic, but still consider them dead

        // Cancel the vanilla damage
        e.setCancelled(true);

        // They're dead by game logic
        sfPlayer.setAlive(false);

        // Clear inventory, reset health, play death sound
        player.getInventory().clear();
        player.setHealth(20);
        playerManager.playSoundToAll(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);

        // Teleport to spectate
        player.teleport(new Location(player.getWorld(), 0, 74, 0)); // Middle of all pillars, at the same height

        playerManager.announceMessage(StringHelpers.format(
                "<red>" + player.getName() + " has been eliminated</red>"
        ));

        // Check for win conditions
        gameManager.checkWinConditions();
    }
}
