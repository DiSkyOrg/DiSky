package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityText extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityText.class,
                String.class,
                "activity (text|content|name)",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "text";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        return activity.getName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
