package me.lunafy.skyfall.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public final class TitleHelpers {
    private TitleHelpers() {}

    public static Title countdownTitle(int seconds)
    {
        return Title.title(
                StringHelpers.format("<red>" + seconds + "</red>"),
                Component.empty(),
                Title.Times.times(
                        Duration.ZERO,
                        Duration.ofSeconds(1),
                        Duration.ofMillis(200)
                )

        );
    }

    public static Title goTitle()
    {
        return Title.title(
                StringHelpers.format("<bold><green>GO!</green></bold>"),
                Component.empty(),
                Title.Times.times(
                        Duration.ZERO,
                        Duration.ofSeconds(2),
                        Duration.ofMillis(500)
                )
        );
    }
}
