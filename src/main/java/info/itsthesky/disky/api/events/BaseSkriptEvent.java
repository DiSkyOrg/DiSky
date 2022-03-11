package info.itsthesky.disky.api.events;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseSkriptEvent extends SelfRegisteringSkriptEvent {

    @Override
    public void register(@NotNull Trigger t) { }

    @Override
    public void unregister(@NotNull Trigger t) { }

    @Override
    public void unregisterAll() { }

    @Override
    public boolean init(Literal<?> @NotNull [] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    public abstract @NotNull String toString();

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return toString();
    }
}
