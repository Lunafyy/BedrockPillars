package me.lunafy.skyfall.game;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.arena.ArenaManager;
import me.lunafy.skyfall.game.item.ItemDistributor;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import me.lunafy.skyfall.util.SkyfallErrors;
import me.lunafy.skyfall.util.StringHelpers;
import me.lunafy.skyfall.util.TitleHelpers;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        updateState(GameState.STARTING);

        int playerCount = playerManager.getPlayerCount();

        arenaManager.generateArena(playerCount);

        List<Location> spawns = arenaManager.getPillars();

        int i = 0;
        for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
        {
            Player p = sfPlayer.getBukkitPlayer();
            if(p == null) continue;

            if(p.getGameMode() == GameMode.SPECTATOR)
            {
                p.setSpectatorTarget(null); // Weird edge-case if a player in spectator has a target, we can't modify their state
            }

            p.setGameMode(GameMode.SURVIVAL);

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

        playerManager.showTitleToAll(TitleHelpers.goTitle());

        updateState(GameState.IN_GAME);

        World arenaWorld = arenaManager.getArenaWorld();
        arenaWorld.playSound(new Location(arenaWorld, 0, 74, 0), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

        itemDistributor.start();
    }

    private void beginStartSequence()
    {
        countdown = 5;

        playerManager.resetPlayers(false);

        for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
        {
            Player p = sfPlayer.getBukkitPlayer();
            if(p == null) continue;

            p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 255), false);
        }

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(countdown == 0)
            {
                startMatch();
                return;
            }

            playerManager.showTitleToAll(TitleHelpers.countdownTitle(countdown));

            // TODO: add some kind of start method that updates the gamestate, enables loot runnables, and triggers the GO title.

            countdown--;
        }, 0L, 20L);
    }

    public void endMatch(SkyfallPlayer winner)
    {
        if(countdownTask != null)
        {
            countdownTask.cancel();
            countdownTask = null;
        }

        for(SkyfallPlayer sfPlayer : playerManager.getPlayers())
        {
            if(sfPlayer == null) return;
            if(sfPlayer.getBukkitPlayer() == null) return;

            Player player = sfPlayer.getBukkitPlayer();
            if(player.getGameMode() == GameMode.SPECTATOR)
            {
                player.setSpectatorTarget(null);
            }

            player.clearActivePotionEffects();
        }

        updateState(GameState.ENDING);
        if(winner != null)
        {
            String winnerName = winner.getBukkitPlayer().getName();

            playerManager.showTitleToAll(TitleHelpers.winnerTitle(winnerName));
        } else {
            playerManager.showTitleToAll(TitleHelpers.drawTitle());
        }

        // cleanup
        Bukkit.getScheduler().runTaskLater(plugin, this::cleanup, 100L);
    }

    public void beginLobbyCountdown(CommandSender starter)
    {
        if(state != GameState.IDLE)
        {
            starter.sendMessage(SkyfallErrors.GAME_ALREADY_RUNNING.component());
            return;
        }

        updateState(GameState.LOBBY_COUNTDOWN);
        countdown = 15;

        Bukkit.broadcast(
                StringHelpers.format("<yellow>A Skyfall game is starting in <red>15 seconds!</red> Use <green>/join</green> to join the game.</yellow>")
        );

        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(countdown == 0)
            {
                // Check if there are at least 2 players before trying to start
                if(playerManager.getPlayerCount() < 2)
                {
                    handleNotEnoughPlayers();
                    return;
                }

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

    }

    private void handleNotEnoughPlayers() {
        playerManager.getPlayers().stream().findFirst().ifPresent(onlyPlayer -> {
            Player player = onlyPlayer.getBukkitPlayer();
            if(player != null)
            {
                player.sendMessage(SkyfallErrors.NOT_ENOUGH_PLAYERS.component());
            }
        });

        cleanup();
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
        plugin.getLogger().info("Gamestate updated to: " + newState.toString());
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

        playerManager.resetPlayers(true);
        arenaManager.resetArena();

        updateState(GameState.IDLE);
    }
}
