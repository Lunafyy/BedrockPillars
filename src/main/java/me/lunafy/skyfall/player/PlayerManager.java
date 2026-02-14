package me.lunafy.skyfall.player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
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

    public void showTitleToAll(Title title)
    {
        for(SkyfallPlayer sfPlayer : this.getPlayers())
        {
            Player player = sfPlayer.getBukkitPlayer();

            if(player == null) continue;

            player.showTitle(title);
        }
    }

    public void playSoundToAll(Sound sound)
    {
        playSoundToAll(sound, null);
    }

    public void playSoundToAll(Sound sound, Location origin)
    {
        for(SkyfallPlayer sfPlayer : players.values())
        {
            Player p = sfPlayer.getBukkitPlayer();
            if(p == null) continue;

            Location soundLocation = (origin != null) ? origin : p.getLocation();
            p.playSound(soundLocation, sound, 1, 1);
        }
    }

    public void resetPlayers(boolean clearPlayerList) {
        for (SkyfallPlayer sfPlayer : players.values()) {
            Player p = sfPlayer.getBukkitPlayer();
            if (p == null) continue;

            p.getInventory().clear();
            p.clearActivePotionEffects();
            p.setFireTicks(0);
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20.0);
            p.setFoodLevel(20);
        }

        if(clearPlayerList) players.clear();
    }

}
