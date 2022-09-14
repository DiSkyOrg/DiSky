package info.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

/**
 * Mean the event is related to a message event with a {@link MessageChannel}
 */
public interface MessageEvent {

    MessageChannel getMessageChannel();

    boolean isFromGuild();

}
