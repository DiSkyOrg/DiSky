package info.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.interactions.components.ComponentInteraction;

public interface ComponentInteractionEvent extends InteractionEvent {

    default ComponentInteraction getComponentInteraction() {
        return (ComponentInteraction) getInteractionEvent();
    };

}
