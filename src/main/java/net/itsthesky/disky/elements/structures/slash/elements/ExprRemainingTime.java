package net.itsthesky.disky.elements.structures.slash.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import net.itsthesky.disky.api.skript.SimpleGetterExpression;
import net.itsthesky.disky.elements.events.rework.CommandEvents;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprRemainingTime extends SimpleGetterExpression<Timespan, Event> {

    static {
        Skript.registerExpression(
                ExprRemainingTime.class,
                Timespan.class,
                ExpressionType.COMBINED,
                "remaining time"
        );
    }

    @Override
    protected String getValue() {
        return "remaining time of the cooldown";
    }

    @Override
    protected Class<Event> getEvent() {
        return (Class<Event>) CommandEvents.SLASH_COOLDOWN_EVENT.getBukkitEventClass();
    }

    @Override
    protected Timespan convert(Event event) {
        final var evt = CommandEvents.SLASH_COOLDOWN_EVENT.getJDAEvent(event);
        if (evt == null)
            return null;

        return new Timespan(evt.getRemainingTime());
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }
}
