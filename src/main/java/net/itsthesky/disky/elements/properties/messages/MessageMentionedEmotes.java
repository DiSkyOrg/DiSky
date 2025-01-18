package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;

@Name("Message Emotes")
@Description({"Get every mentioned emotes in a message.",
"This will only return custom emote, and will therefore not include discord emotes."})
@Examples("mentioned emotes of event-message")
public class MessageMentionedEmotes extends MultiplyPropertyExpression<Message, Emote> {

    static {
        register(
                MessageMentionedEmotes.class,
                Emote.class,
                "[discord] [message] mentioned emote[s]",
                        "message"
        );
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned emotes";
    }

    @Override
    protected Emote[] convert(Message message) {
        return message.getMentions().getCustomEmojis()
                .stream()
                .map(Emote::new)
                .toArray(Emote[]::new);
    }
}
