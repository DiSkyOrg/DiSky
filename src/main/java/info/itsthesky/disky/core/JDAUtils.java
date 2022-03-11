package info.itsthesky.disky.core;

import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.ArrayList;
import java.util.List;

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

	public static Component[] convert(ComponentRow[] rows) {
		final List<Component> components = new ArrayList<>();
		for (ComponentRow row : rows)
			components.addAll(row.asComponents());
		return components.toArray(new Component[0]);
	}
}
