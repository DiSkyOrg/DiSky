package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
