package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Activity Details")
@Description({"Returns the details line of a rich presence activity (the first line shown under the activity name).",
        "Returns nothing if the activity is not a rich presence or has no details set."})
@Examples({"set {_details} to activity details of event-activity",
        "reply with activity details of event-activity"})
@Since("4.29.0")
public class ActivityDetails extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityDetails.class,
                String.class,
                "activity details",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity details";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        return activity.asRichPresence().getDetails();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}