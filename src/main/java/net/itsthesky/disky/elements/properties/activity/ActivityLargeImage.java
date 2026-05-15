package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Activity Large Image URL")
@Description({"Returns the URL of the large image of a rich presence activity.",
        "Returns nothing if the activity is not a rich presence or has no large image set."})
@Examples({"set {_image} to activity large image of event-activity",
        "reply with activity large image url of event-activity"})
@Since("4.29.0")
public class ActivityLargeImage extends SimplePropertyExpression<Activity, String> {

    static {
        register(
                ActivityLargeImage.class,
                String.class,
                "activity large image [url]",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity large image";
    }

    @Override
    public @Nullable String convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        RichPresence.Image image = activity.asRichPresence().getLargeImage();
        if (image == null) return null;
        return image.getUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}