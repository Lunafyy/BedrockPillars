package me.lunafy.skyfall.commands;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.util.SkyfallErrors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StartGame implements CommandExecutor {
    private final Skyfall plugin;

    public StartGame(Skyfall plugin)
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

        if(!player.hasPermission("skyfall.admin.start"))
        {
            player.sendMessage(SkyfallErrors.NO_PERMISSION.component());
            return true;
        }

        plugin.getPlayerManager().addPlayer(player);

        plugin.getGameManager().beginLobbyCountdown(player);

        return true;
    }
}
