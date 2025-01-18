package net.itsthesky.disky.elements.properties.emotes;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Emote Guild")
@Description({"Get the guild that is attached to this emote.",
        "Only emote have guild, emoji are from Discord and will therefore return none here."
})
@Examples({"emote guild of event-emote",
        "emote guild of reaction \"disky\" # Custom emoji only"})
public class EmoteGuild extends SimplePropertyExpression<Emote, Guild> {

    static {
        /*
        register(
                EmoteGuild.class,
                Guild.class,
                "[the] emo(te|ji) guild",
                "emote"
        );*/
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "emote guild";
    }

    @Override
    public @Nullable Guild convert(Emote emote) {
        return emote.getGuild();
    }

    @Override
    public @NotNull Class<? extends Guild> getReturnType() {
        return Guild.class;
    }

}
