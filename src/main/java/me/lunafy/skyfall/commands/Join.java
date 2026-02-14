package me.lunafy.skyfall.commands;

import me.lunafy.skyfall.Skyfall;
import me.lunafy.skyfall.game.GameManager;
import me.lunafy.skyfall.game.GameState;
import me.lunafy.skyfall.player.PlayerManager;
import me.lunafy.skyfall.util.SkyfallErrors;
import me.lunafy.skyfall.util.StringHelpers;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Join implements CommandExecutor {
    private final GameManager gameManager;
    private final PlayerManager playerManager;

    public Join(Skyfall plugin)
    {
        this.gameManager = plugin.getGameManager();
        this.playerManager = plugin.getPlayerManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if(!(sender instanceof Player player))
        {
            sender.sendMessage(SkyfallErrors.PLAYERS_ONLY.component());
            return true;
        }

        if(gameManager.getState() == GameState.IDLE)
        {
            player.sendMessage(SkyfallErrors.NO_ACTIVE_GAME.component());
            return true;
        }

        if(gameManager.getState() != GameState.LOBBY_COUNTDOWN)
        {
            player.sendMessage(SkyfallErrors.GAME_ALREADY_RUNNING.component());
            return true;
        }

        if(playerManager.hasPlayer(player))
        {
            player.sendMessage(SkyfallErrors.ALREADY_IN_QUEUE.component());
            return true;
        }

        playerManager.addPlayer(player);

        player.sendMessage(StringHelpers.format("<green>You have been added to the Skyfall game!</green>"));

        return true;
    }
}
