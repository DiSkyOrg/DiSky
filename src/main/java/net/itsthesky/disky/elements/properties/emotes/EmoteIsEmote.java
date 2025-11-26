package net.itsthesky.disky.elements.properties.emotes;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.api.emojis.Emote;
import org.jetbrains.annotations.NotNull;

@Name("EmoteIsEmote")
@Description("Check if an emote is a custom emote or not.")
@Examples({"if {_emote} is an emote:",
        "\treply with \"This is a custom emote!\""})
@Since("4.0.0")
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
