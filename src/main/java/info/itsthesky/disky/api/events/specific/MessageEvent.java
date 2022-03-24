package info.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

/**
 * Mean the event is related to a message event with a {@link MessageChannel}
 */
public interface MessageEvent {

    GenericMessageEvent getMessageEvent();

    default MessageChannel getMessageChannel() {
        return getMessageEvent().getChannel();
    };

}
