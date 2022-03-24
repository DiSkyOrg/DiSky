package info.itsthesky.disky.api.events.specific;

import net.dv8tion.jda.api.interactions.components.text.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;

import javax.annotation.Nonnull;

public interface ModalEvent {

	ModalCallbackAction replyModal(@Nonnull Modal modal);

}
