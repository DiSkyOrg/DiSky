package info.itsthesky.disky.api.skript;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ErrorHandler {

    default void exception(@Nullable Event event, @NotNull String message) {
        exception(event, new RuntimeException(message));
    }

    void exception(@Nullable Event event, @Nullable Throwable error);

    void insertErrorValue(@NotNull Event event, @Nullable Throwable error);

    @Nullable Throwable getErrorValue(@NotNull Event event);

}
