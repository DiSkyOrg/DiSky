package info.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ActivityType extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityType.class,
                String.class,
                "activity type",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity type";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        return activity.getType().name().toLowerCase(Locale.ROOT).replaceAll("_", " ");
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
