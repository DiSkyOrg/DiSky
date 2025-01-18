package net.itsthesky.disky.elements.properties.reactions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.emojis.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReactionEmote extends SimplePropertyExpression<MessageReaction, Emote> {

    static {
        register(
                ReactionEmote.class, Emote.class,
                "[the] [reaction] emote",
                "reaction"
        );
    }

    @Override
    public @Nullable Emote convert(MessageReaction messageReaction) {
        return Emote.fromUnion(messageReaction.getEmoji());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "reaction emote";
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }
}
