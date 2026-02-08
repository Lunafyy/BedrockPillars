package me.lunafy.skyfall.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public class SkyfallPlayer {
    private final UUID uuid;

    private boolean alive = true;

    public SkyfallPlayer(Player player)
    {
        // Store UUID to avoid accidental NPEs / offline player issues
        this.uuid = player.getUniqueId();
    }

    public boolean isAlive()
    {
        return alive;
    }

    public @Nullable Player getBukkitPlayer()
    {
        return Bukkit.getPlayer(uuid);
    }

    public void setAlive(boolean alive)
    {
        if(this.alive == alive) return;

        this.alive = alive;

        if(!alive)
        {
            Player player = getBukkitPlayer();
            if(player == null) return; // In case the death is due to a logout

            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.setHealth(20);
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;

        if(!(o instanceof SkyfallPlayer other)) return false;

        return other.uuid.equals(uuid);
    }

    @Override
    public int hashCode()
    {
        return uuid.hashCode();
    }
}
