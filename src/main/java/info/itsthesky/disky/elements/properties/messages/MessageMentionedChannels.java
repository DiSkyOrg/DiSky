package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

@Name("Message Text Channels")
@Description("Get every mentioned text channels in a message.")
@Examples("mentioned channels of event-message")
public class MessageMentionedChannels extends MultiplyPropertyExpression<Message, TextChannel> {

    static {
        register(
                MessageMentionedChannels.class,
                TextChannel.class,
                "[discord] [message] mentioned [text] channels",
                "message"
        );
    }

    @Override
    public @NotNull Class<? extends TextChannel> getReturnType() {
        return TextChannel.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned channels";
    }

    @Override
    protected TextChannel[] convert(Message t) {
        return t.getMentionedChannels().toArray(new TextChannel[0]);
    }
}
