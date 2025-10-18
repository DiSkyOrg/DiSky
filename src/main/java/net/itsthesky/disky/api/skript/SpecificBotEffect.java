package net.itsthesky.disky.api.skript;

import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SpecificBotEffect<T> extends WaiterEffect<T> {

    public abstract void runEffect(@NotNull Event e, @NotNull Bot bot);

    @Override
    public void runEffect(@NotNull Event e) {
        final Bot bot = getBot();
        if (bot == null) {
            DiSkyRuntimeHandler.error(new RuntimeException("No bot is currently loaded on the server. You cannot use any DiSky syntaxes without least one loaded."));
            return;
        }

        runEffect(e, bot);
    }

    public @Nullable Bot getBot() {
        return DiSky.getManager().findAny();
    }
}
