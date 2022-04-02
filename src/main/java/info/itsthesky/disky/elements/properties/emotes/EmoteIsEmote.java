package info.itsthesky.disky.elements.properties.emotes;

import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.api.skript.PropertyCondition;
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
        return isNegated() != emote.isEmote();
    }

    @Override
    protected String getPropertyName() {
        return "an emote";
    }

}
