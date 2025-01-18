package net.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;

/**
 * Mean this event is related to interaction (buttons or slash command currently) with an {@link net.dv8tion.jda.api.interactions.Interaction} entity
 */
public interface InteractionEvent extends MessageEvent {

    GenericInteractionCreateEvent getInteractionEvent();

    @Override
    default MessageChannel getMessageChannel() {
        return getInteractionEvent().getMessageChannel();
    }

    @Override
    default boolean isFromGuild() {
        return getInteractionEvent().isFromGuild();
    }
}
