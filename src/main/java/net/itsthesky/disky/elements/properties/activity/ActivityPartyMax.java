package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityPartyMax extends SimplePropertyExpression<Activity, Long> {

    static {
        register(
                ActivityPartyMax.class,
                Long.class,
                "activity party max [size]",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity party max size";
    }

    @Override
    public @Nullable Long convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        RichPresence.Party party = activity.asRichPresence().getParty();
        if (party == null) return null;
        long max = party.getMax();
        return max == -1 ? null : max;
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }
}
