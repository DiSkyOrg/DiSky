package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

@Name("Message Voice Channels")
@Description("Get every mentioned voice channels in a message.")
@Examples("mentioned voice channels of event-message")
public class MessageMentionedVoiceChannels extends MultiplyPropertyExpression<Message, VoiceChannel> {

    static {
        register(
                MessageMentionedVoiceChannels.class,
                VoiceChannel.class,
                "[discord] [message] mentioned voice channels",
                "message"
        );
    }

    @Override
    public @NotNull Class<? extends VoiceChannel> getReturnType() {
        return VoiceChannel.class;
    }

    @Override
    protected String getPropertyName() {
        return "mentioned channels";
    }

    @Override
    protected VoiceChannel[] convert(Message t) {
        return t.getMentions().getChannels().toArray(new VoiceChannel[0]);
    }
}
