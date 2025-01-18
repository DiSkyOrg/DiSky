package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import org.jetbrains.annotations.NotNull;

@Name("Message Reactions")
@Description({"Get every reactions of a message.",
"Because of Discord's limitation, we cannot get which user reacted with which reaction, you'll have to count them yourself."})
@Examples("reactions of event-message")
public class MessageReactions extends MultiplyPropertyExpression<Message, Emote> {

    static {
        register(
                MessageReactions.class,
                Emote.class,
                "[discord] [message] (emo(te|ji)|reaction)[s]",
                "message"
        );
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }

    @Override
    protected String getPropertyName() {
        return "emotes";
    }

    @Override
    protected Emote[] convert(Message original) {
        return original
                .getReactions()
                .stream()
                .map(MessageReaction::getEmoji)
                .map(Emote::new)
                .toArray(Emote[]::new);
    }
}
