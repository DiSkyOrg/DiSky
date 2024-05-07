package info.itsthesky.disky.elements.structures.slash.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.util.Timespan;
import info.itsthesky.disky.api.skript.SimpleGetterExpression;
import org.jetbrains.annotations.NotNull;

public class ExprRemainingTime extends SimpleGetterExpression<Timespan, OnCooldownEvent.BukkitCooldownEvent> {

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
    protected Class<OnCooldownEvent.BukkitCooldownEvent> getEvent() {
        return OnCooldownEvent.BukkitCooldownEvent.class;
    }

    @Override
    protected Timespan convert(OnCooldownEvent.BukkitCooldownEvent event) {
        final long remaining = event.getRemainingTime();
        return new Timespan(remaining);
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }
}
