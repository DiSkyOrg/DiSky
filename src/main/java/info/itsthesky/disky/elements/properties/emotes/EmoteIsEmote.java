package info.itsthesky.disky.elements.properties.emotes;

import info.itsthesky.disky.api.emojis.Emote;
import ch.njol.skript.conditions.base.PropertyCondition;
import org.jetbrains.annotations.NotNull;

public class EmoteIsEmote extends PropertyCondition<Emote> {

    static {
        register(
                EmoteIsEmote.class,
                PropertyType.BE,
                "[a[n]] emote",
                "emote"
        );
    }

    @Override
    public boolean check(@NotNull Emote emote) {
        return isNegated() != emote.isCustom();
    }

    @Override
    protected String getPropertyName() {
        return "an emote";
    }

}
