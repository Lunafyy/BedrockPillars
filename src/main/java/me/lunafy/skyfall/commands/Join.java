package me.lunafy.skyfall.commands;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.util.SkyfallErrors;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Join implements CommandExecutor {
    private final Skyfall plugin;

    public Join(Skyfall plugin)
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

        if(plugin.getGameManager().getState() != GameState.LOBBY_COUNTDOWN)
        {
            player.sendMessage(SkyfallErrors.GAME_ALREADY_RUNNING.component());
            return true;
        }

        if(plugin.getPlayerManager().hasPlayer(player))
        {
            player.sendMessage(SkyfallErrors.ALREADY_IN_QUEUE.component());
            return true;
        }

        plugin.getPlayerManager().addPlayer(player);

        player.sendMessage(StringHelpers.format("<green>You have been added to the Skyfall game!</green>"));

        return true;
    }
}
