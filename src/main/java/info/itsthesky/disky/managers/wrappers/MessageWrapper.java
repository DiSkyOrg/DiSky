package info.itsthesky.disky.managers.wrappers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

/**
 * Wrapper for the message object.
 */
public class MessageWrapper extends ReceivedMessage {

	public MessageWrapper(Message message) {
		super(message.getIdLong(),
				message.getChannel(),
				message.getType(),
				message.getMessageReference(),
				false,
				message.getApplicationIdLong(),
				message.isTTS(),
				message.isPinned(),
				message.getContentRaw(),
				message.getNonce(),
				message.getAuthor(),
				message.getMember(),
				message.getActivity(),
				message.getTimeEdited(),
				message.getMentions(),
				message.getReactions(),
				message.getAttachments(),
				message.getEmbeds(),
				message.getStickers(),
				message.getActionRows(),
				MessageFlag.toBitField(message.getFlags()),
				message.getInteraction(),
				message.getStartedThread(),
				-1);
	}

}
