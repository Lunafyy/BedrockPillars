package me.lunafy.skyfall.player;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {
    /*
    Using a map to achieve O(1) lookups, compared to O(n) by using a list
     */
    private final Map<UUID, SkyfallPlayer> players = new HashMap<>();

    public void addPlayer(Player player)
    {
        players.putIfAbsent(player.getUniqueId(), new SkyfallPlayer(player));
    }

    public SkyfallPlayer getPlayer(UUID uuid)
    {
        return players.get(uuid);
    }

    public SkyfallPlayer getPlayer(Player player)
    {
        return players.get(player.getUniqueId());
    }

    public boolean hasPlayer(Player player)
    {
        return players.containsKey(player.getUniqueId());
    }

    public Collection<SkyfallPlayer> getPlayers()
    {
        return players.values();
    }

    public void removePlayer(Player player)
    {
        players.remove(player.getUniqueId());
    }

    public int getPlayerCount()
    {
        return players.size();
    }

    public Collection<SkyfallPlayer> getAlivePlayers()
    {
        return players.values()
                .stream()
                .filter(SkyfallPlayer::isAlive)
                .toList();
    }

    public void announceMessage(Component message)
    {
        for(SkyfallPlayer sfPlayer : this.getPlayers())
        {
            Player player = sfPlayer.getBukkitPlayer();

            if(player == null) continue;

            player.sendMessage(message);
        }
    }
}
