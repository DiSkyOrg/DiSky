package net.itsthesky.disky.managers.wrappers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.internal.entities.ReceivedMessage;

/**
 * Wrapper for the message object.
 */
public class MessageWrapper extends ReceivedMessage {

	public MessageWrapper(Message message) {
		super(message.getIdLong(),
				message.getChannelIdLong(),
				message.getGuildIdLong(),
				message.getJDA(),
				message.getGuild(),
				message.getChannel(),
				message.getType(),
				message.getMessageReference(),
				message.isWebhookMessage(),
				message.getApplicationIdLong(),
				message.isTTS(),
				message.isPinned(),
				message.getContentRaw(),
				message.getNonce(),
				message.getAuthor(),
				message.getMember(),
				message.getActivity(),
				message.getPoll(),
				message.getTimeEdited(),
				message.getMentions(),
				message.getReactions(),
				message.getAttachments(),
				message.getEmbeds(),
				message.getStickers(),
				message.getComponents(),
				message.getMessageSnapshots(),
				MessageFlag.toBitField(message.getFlags()),
				message.getInteraction(),
				message.getStartedThread(),
				-1);
	}

}
