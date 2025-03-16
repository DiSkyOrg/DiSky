package net.itsthesky.disky.core;

import ch.njol.skript.Skript;
import net.itsthesky.disky.DiSky;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.jetbrains.annotations.NotNull;

/**
 * Class related to the bStats metrics system.
 */
public final class DiSkyMetrics {

    public static void init(@NotNull DiSky disky) {
        final var metrics = new Metrics(disky, 24505);

        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));
        metrics.addCustomChart(new SimplePie("using_slash_structures", () -> {
            boolean using = false;
            for (final var bot : DiSky.getManager().getBots()) {
                if (!bot.getSlashManager().getRegisteredGroups().isEmpty()) {
                    using = true;
                    break;
                }
            }

            return using ? "Yes" : "No";
        }));
        metrics.addCustomChart(new SimplePie("using_context_structures", () -> {
            boolean using = false;
            for (final var bot : DiSky.getManager().getBots()) {
                if (!bot.getContextManager().getRegisteredCommands().isEmpty()) {
                    using = true;
                    break;
                }
            }

            return using ? "Yes" : "No";
        }));
    }

}