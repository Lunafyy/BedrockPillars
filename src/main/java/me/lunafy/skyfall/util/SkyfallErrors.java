package me.lunafy.skyfall.util;

import net.kyori.adventure.text.Component;

public enum SkyfallErrors {
    NO_PERMISSION("<red>You don't have permission to do that.</red>"),
    PLAYERS_ONLY("<red>Only players can run this command.</red>"),
    GAME_ALREADY_RUNNING("<red>There is already a Skyfall game in progress.</red>"),
    ALREADY_IN_QUEUE("<red>You are already in the queue for this game!</red>"),
    NO_ACTIVE_GAME("<red>There is no active Skyfall game.</red>"),
    NOT_ENOUGH_PLAYERS("<red>There were not enough players to start this game!</red>");

    private final String message;

    SkyfallErrors(String message)
    {
        this.message = message;
    }

    public Component component()
    {
        return StringHelpers.format(message);
    }

    public String raw()
    {
        return message;
    }
}
