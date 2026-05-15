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

@Name("Activity Small Image URL")
@Description({"Returns the URL of the small image of a rich presence activity.",
        "Returns nothing if the activity is not a rich presence or has no small image set."})
@Examples({"set {_image} to activity small image of event-activity",
        "reply with activity small image url of event-activity"})
@Since("4.29.0")
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