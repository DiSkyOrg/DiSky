package net.itsthesky.disky.elements.properties.emotes;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.api.emojis.Emote;
import org.jetbrains.annotations.NotNull;

@Name("EmoteIsAnimated")
@Description("Check if an emote is animated or not.")
@Examples({"if {_emote} is animated:",
        "\treply with \"This emote is animated!\""})
@Since("4.0.0")
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
