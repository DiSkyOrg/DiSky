package info.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityURL extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityURL.class,
                String.class,
                "activity ur(i|l)",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity url";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        return activity.getUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
