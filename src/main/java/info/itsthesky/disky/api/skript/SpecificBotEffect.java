package info.itsthesky.disky.api.skript;

import de.leonhard.storage.shaded.jetbrains.annotations.NotNull;
import de.leonhard.storage.shaded.jetbrains.annotations.Nullable;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;

public abstract class SpecificBotEffect<T> extends WaiterEffect<T> {

    public abstract void runEffect(@NotNull Event e, @NotNull Bot bot);

    @Override
    public void runEffect(Event e) {
        final Bot bot = getBot();
        if (bot == null) {
            DiSky.getErrorHandler().exception(new RuntimeException("No bot is currently loaded on the server. You cannot use any DiSky syntaxes without least one loaded."));
            restart();
            return;
        }
        runEffect(e, bot);
    }

    public @Nullable Bot getBot() {
        return DiSky.getManager().findAny();
    }
}
