package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
