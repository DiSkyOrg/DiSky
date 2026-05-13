package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

public class ActivityTimestampStart extends SimplePropertyExpression<Activity, Date> {

    static {
        register(
                ActivityTimestampStart.class,
                Date.class,
                "activity start [time[stamp]]",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity start timestamp";
    }

    @Override
    public @Nullable Date convert(@NotNull Activity activity) {
        Activity.Timestamps timestamps = activity.getTimestamps();
        if (timestamps == null) return null;
        long start = timestamps.getStart();
        return start == 0 ? null : new Date(start, TimeZone.getTimeZone("GMT"));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}
