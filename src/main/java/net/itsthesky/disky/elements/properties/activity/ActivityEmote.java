package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityEmote extends SimplePropertyExpression<Activity, Emote> {

    static {
        register(
                ActivityEmote.class,
                Emote.class,
                "activity emo(ji|te)",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity emote";
    }

    @Override
    public @Nullable Emote convert(@NotNull Activity activity) {
        return activity.getEmoji() == null ? null : Emote.fromUnion(activity.getEmoji());
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }
}
