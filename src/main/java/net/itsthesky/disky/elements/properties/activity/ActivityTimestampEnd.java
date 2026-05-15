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

@Name("Activity End Timestamp")
@Description({"Returns the end timestamp of an activity (typically used to show remaining time in rich presences).",
        "Returns nothing if the activity has no timestamps or no end time set."})
@Examples({"set {_end} to activity end timestamp of event-activity",
        "reply with activity end time of event-activity"})
@Since("4.29.0")
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
        Instant end = timestamps.getEndTime();
        return end == null ? null : SkriptUtils.convertDateTime(end.atOffset(ZoneOffset.UTC));
    }

    @Override
    public @NotNull Class<? extends Date> getReturnType() {
        return Date.class;
    }
}