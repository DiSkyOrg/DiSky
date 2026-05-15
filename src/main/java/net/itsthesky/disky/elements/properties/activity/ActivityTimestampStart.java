package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Activity;
import net.itsthesky.disky.core.SkriptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneOffset;

@Name("Activity Start Timestamp")
@Description({"Returns the start timestamp of an activity (typically used to show elapsed time in rich presences).",
        "Returns nothing if the activity has no timestamps or no start time set."})
@Examples({"set {_start} to activity start timestamp of event-activity",
        "reply with activity start time of event-activity"})
@Since("4.29.0")
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
        Instant start = timestamps.getStartTime();
        return start == null ? null : SkriptUtils.convertDateTime(start.atOffset(ZoneOffset.UTC));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}