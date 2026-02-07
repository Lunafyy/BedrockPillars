package me.lunafy.skyfall.game.item;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.player.SkyfallPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemDistributor {
    private final Skyfall plugin;
    private final PlayerManager playerManager;
    private BukkitTask task;

    private static final long INTERVAL_TICKS = 20L * 5;
    private final Random random = new Random();

    private static final List<Material> MATERIALS =
            Arrays.stream(Material.values())
                    .filter(Material::isItem)
                    .filter(m -> !m.isBlock()) // ← kills potted plants dead
                    .filter(m -> m != Material.AIR)
                    .toList();

    public ItemDistributor(Skyfall plugin)
    {
        this.plugin = plugin;
        playerManager = plugin.getPlayerManager();
    }

    public void start()
    {
        if(task != null) return;

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::giveItems,
                0L,
                INTERVAL_TICKS
        );
    }

    public void stop()
    {
        if(task != null)
        {
            task.cancel();
            task = null;
        }
    }

    private void giveItems()
    {
        for(SkyfallPlayer sfPlayer : playerManager.getAlivePlayers())
        {
            Player player = sfPlayer.getBukkitPlayer();
            if(player == null) continue;

            player.getInventory().addItem(randomItem());
        }
    }

    private ItemStack randomItem()
    {
        return new ItemStack(
                MATERIALS.get(random.nextInt(MATERIALS.size()))
        );
    }
}
