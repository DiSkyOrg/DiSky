package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivitySmallImage extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivitySmallImage.class,
                String.class,
                "activity small image [url]",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity small image";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        RichPresence.Image image = activity.asRichPresence().getSmallImage();
        if (image == null) return null;
        return image.getUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
