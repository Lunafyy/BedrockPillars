package me.lunafy.skyfall.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class StringHelpers {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Private constructor to stop instantiation
    private StringHelpers() {}

    /**
     * Uses MiniMessage to format a given string
     */
    public static Component format(String message)
    {
        return MINI_MESSAGE.deserialize(message);
    }
}
