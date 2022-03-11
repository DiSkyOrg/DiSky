package info.itsthesky.disky.core;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

public final class JDAUtils {

	public static MessageBuilder constructMessage(Object input) {
		if (input == null)
			return null;
		final MessageBuilder builder = new MessageBuilder();
		if (input instanceof MessageBuilder)
			return (MessageBuilder) input;
		if (input instanceof String)
			builder.append((String) input);
		if (input instanceof EmbedBuilder)
			builder.setEmbeds(((EmbedBuilder) input).build());
		return builder;
	}

}
