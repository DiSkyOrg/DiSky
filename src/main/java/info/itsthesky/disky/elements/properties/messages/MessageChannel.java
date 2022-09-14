package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Message Text Channel")
@Description("Get the text channel were the message was sent. Can be null if it's in PM or not in guild!")
@Examples("channel of event-message")
public class MessageChannel extends SimplePropertyExpression<Message, TextChannel> {

    static {
        register(
                MessageChannel.class,
                TextChannel.class,
                "[discord] [message] [text]( |-)channel",
                "message"
        );
    }


    @Override
    protected @NotNull String getPropertyName() {
        return "channel";
    }

    @Override
    public @Nullable TextChannel convert(Message original) {
        if (original.isFromGuild())
            return original.getChannel().asTextChannel();
        return null;
    }

    @Override
    public @NotNull Class<? extends TextChannel> getReturnType() {
        return TextChannel.class;
    }
}
