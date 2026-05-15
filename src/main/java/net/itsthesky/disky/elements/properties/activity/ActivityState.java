package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Activity State")
@Description({"Returns the state line of an activity (the second line shown under the activity name).",
        "Returns nothing if the activity has no state set."})
@Examples({"set {_state} to activity state of event-activity",
        "reply with activity state of event-activity"})
@Since("4.29.0")
public class ActivityState extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityState.class,
                String.class,
                "activity state",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity state";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        return activity.getState();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}