package net.itsthesky.disky.elements.properties.activity;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.RichPresence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ActivityPartySize extends SimplePropertyExpression<Activity, Long> {

    static {
        register(
                ActivityPartySize.class,
                Long.class,
                "activity party (size|current size)",
                "activity"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "activity party size";
    }

    @Override
    public @Nullable Long convert(@NotNull Activity activity) {
        if (!activity.isRich()) return null;
        RichPresence.Party party = activity.asRichPresence().getParty();
        if (party == null) return null;
        long size = party.getSize();
        return size == -1 ? null : size;
    }

    @Override
    public @NotNull Class<? extends Long> getReturnType() {
        return Long.class;
    }
}
