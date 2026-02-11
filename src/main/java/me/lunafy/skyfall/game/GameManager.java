package me.lunafy.skyfall.game;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.arena.ArenaManager;
import me.lunafy.skyfall.game.item.ItemDistributor;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.SkyfallErrors;
import me.lunafy.skyfall.util.StringHelpers;
import me.lunafy.skyfall.util.TitleHelpers;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameManager {
    private Skyfall plugin;
    private PlayerManager playerManager;
    private ArenaManager arenaManager;
    private ItemDistributor itemDistributor;

    private GameState state = GameState.IDLE;
    private BukkitTask countdownTask;
    private int countdown = 15;

    public GameManager(Skyfall plugin)
    {
        this.plugin = plugin;
        this.playerManager = plugin.getPlayerManager();
        this.arenaManager = plugin.getArenaManager();
        this.itemDistributor = new ItemDistributor(plugin);
    }

    public void prepareArena()
    {
        countdownTask.cancel();
        state = GameState.STARTING;

        int playerCount = playerManager.getPlayerCount();

        arenaManager.generateArena(playerCount);

        List<Location> spawns = arenaManager.getPillars();

        int i = 0;
        for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
        {
            Player p = sfPlayer.getBukkitPlayer();
            if(p == null) continue;

            p.teleport(
                    spawns.get(i++)
                            .clone()
                            .add(0.5, 10, 0.5)
            );
        }

        beginStartSequence();
    }

    private void startMatch()
    {
        countdownTask.cancel();

        state = GameState.IN_GAME;

        World arenaWorld = arenaManager.getArenaWorld();
        arenaWorld.playSound(new Location(arenaWorld, 0, 74, 0), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

        itemDistributor.start();
    }

    private void beginStartSequence()
    {
        countdown = 5;

        for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
        {
            Player p = sfPlayer.getBukkitPlayer();
            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255), false);
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(countdown == 0)
            {
                startMatch();
                return;
            }

            for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
            {
                Player p = sfPlayer.getBukkitPlayer();
                if(p == null) continue;

                p.showTitle(TitleHelpers.countdownTitle(countdown));
            }
            // TODO: add some kind of start method that updates the gamestate, enables loot runnables, and triggers the GO title.


            countdown--;
        }, 0L, 20L);
    }

    public void endMatch(SkyfallPlayer winner)
    {
        state = GameState.ENDING;

        if(winner != null)
        {
            String winnerName = winner.getBukkitPlayer().getName();

            for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
            {
                Player player = sfPlayer.getBukkitPlayer();
                if(player == null) continue; // Disconnected players

                player.showTitle(TitleHelpers.winnerTitle(winnerName));
            }

            Bukkit.getScheduler().runTaskLater(plugin, this::cleanup, 100L);
        }

        // cleanup
        state = GameState.IDLE;
    }

    public boolean beginLobbyCountdown(CommandSender starter)
    {
        if(state != GameState.IDLE)
        {
            starter.sendMessage(SkyfallErrors.GAME_ALREADY_RUNNING.component());
            return false;
        }

        state = GameState.LOBBY_COUNTDOWN;
        countdown = 15;

        Bukkit.broadcast(
                StringHelpers.format("<yellow>A Skyfall game is starting in <red>15 seconds!</red> Use <green>/join</green> to join the game.</yellow>")
        );

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(countdown == 0)
            {
                prepareArena();
                return;
            }

            if(countdown <= 5 || countdown % 5 == 0)
            {
                Bukkit.broadcast(
                        StringHelpers.format("<yellow>Starting in <red>" + countdown + " seconds<red>...")
                );
            }

            countdown--;
        }, 20L, 20L);

        return true;
    }

    public void checkWinConditions()
    {
        List<SkyfallPlayer> alive = new ArrayList<>(playerManager.getAlivePlayers());

        if(alive.size() <= 1)
        {
            endMatch(alive.isEmpty() ? null : alive.getFirst());
        }
    }

    public void updateState(GameState newState)
    {
        state = newState;
    }

    public GameState getState()
    {
        return state;
    }

    public void cleanup()
    {
        if(countdownTask != null)
        {
            countdownTask.cancel();
            countdownTask = null;
        }

        if(itemDistributor != null)
        {
            itemDistributor.stop();
        }

        countdown = 15;

        state = GameState.IDLE;

        playerManager.resetPlayers();
        arenaManager.resetArena();
    }
}
