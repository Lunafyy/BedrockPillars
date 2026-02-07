package me.lunafy.skyfall.commands;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.arena.ArenaManager;
import me.lunafy.skyfall.util.SkyfallErrors;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GenerateWorld implements CommandExecutor {
    private final Skyfall plugin;

    public GenerateWorld(Skyfall plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(sender instanceof Player player))
        {
            sender.sendMessage(SkyfallErrors.PLAYERS_ONLY.component());
            return true;
        }

        plugin.getArenaManager().generateArena(5);

        Location spawn = plugin.getArenaManager()
                .getPillars()
                .get(0)
                .clone()
                .add(new Vector(0.5, 11, 0.5));

        player.teleport(spawn);

        return true;
    }
}
