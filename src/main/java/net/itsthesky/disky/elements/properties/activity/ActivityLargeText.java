package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityLargeText extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityLargeText.class,
                String.class,
                "activity large [image] text",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity large text";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        RichPresence.Image image = activity.asRichPresence().getLargeImage();
        if (image == null) return null;
        return image.getText();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
