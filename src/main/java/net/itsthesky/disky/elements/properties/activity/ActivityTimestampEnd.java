package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

public class ActivityTimestampEnd extends SimplePropertyExpression<Activity, Date> {

    static {
        register(
                ActivityTimestampEnd.class,
                Date.class,
                "activity end [time[stamp]]",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity end timestamp";
    }

    @Override
    public @Nullable Date convert(@NotNull Activity activity) {
        Activity.Timestamps timestamps = activity.getTimestamps();
        if (timestamps == null) return null;
        long end = timestamps.getEnd();
        return end == 0 ? null : new Date(end, TimeZone.getTimeZone("GMT"));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}
