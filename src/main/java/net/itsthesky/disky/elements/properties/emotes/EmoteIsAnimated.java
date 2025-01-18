package net.itsthesky.disky.elements.properties.emotes;

import ch.njol.skript.conditions.base.PropertyCondition;
import net.itsthesky.disky.api.emojis.Emote;
import org.jetbrains.annotations.NotNull;

public class EmoteIsAnimated extends PropertyCondition<Emote> {

    static {
        register(
                EmoteIsAnimated.class,
                PropertyType.BE,
                "animated",
                "emote"
        );
    }

    @Override
    public boolean check(@NotNull Emote emote) {
        return isNegated() != emote.isAnimated();
    }

    @Override
    protected String getPropertyName() {
        return "animated";
    }

}
