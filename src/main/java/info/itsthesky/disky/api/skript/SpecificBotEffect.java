package info.itsthesky.disky.api.skript;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SpecificBotEffect<T> extends WaiterEffect<T> {

    public abstract void runEffect(@NotNull Event e, @NotNull Bot bot);

    @Override
    public void runEffect(Event e) {
        final Bot bot = getBot();
        if (bot == null) {
            DiSky.getErrorHandler().exception(e, new RuntimeException("No bot is currently loaded on the server. You cannot use any DiSky syntaxes without least one loaded."));
            restart();
            return;
        }
        runEffect(e, bot);
    }

    public @Nullable Bot getBot() {
        return DiSky.getManager().findAny();
    }
}
