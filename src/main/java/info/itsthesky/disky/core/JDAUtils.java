package info.itsthesky.disky.core;

import info.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.internal.utils.Helpers;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public final class JDAUtils {

	public static MessageCreateBuilder constructCreateMessage(Object input) {
		if (input == null)
			return null;
		final MessageCreateBuilder builder = new MessageCreateBuilder();
		if (input instanceof MessageCreateBuilder)
			return (MessageCreateBuilder) input;
		if (input instanceof String)
			builder.addContent((String) input);
		if (input instanceof EmbedBuilder)
			builder.setEmbeds(((EmbedBuilder) input).build());
		return builder;
	}

	public static MessageEditBuilder constructEditMessage(Object input) {
		if (input == null)
			return null;
		final MessageEditBuilder builder = new MessageEditBuilder();
		if (input instanceof MessageEditBuilder)
			return (MessageEditBuilder) input;
		if (input instanceof String)
			builder.setContent((String) input);
		if (input instanceof EmbedBuilder)
			builder.setEmbeds(((EmbedBuilder) input).build());
		return builder;
	}

	public static Icon parseIcon(String value) {
		final InputStream iconStream;
		if (Utils.isURL(value)) {
			try {
				iconStream = new URL(value).openStream();
			} catch (IOException ioException) {
				ioException.printStackTrace();
				return null;
			}
		} else {
			final File iconFile = new File(value);
			if (iconFile == null || !iconFile.exists())
				return null;
			try {
				iconStream = new FileInputStream(iconFile);
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
				return null;
			}
		}
		try {
			return Icon.from(iconStream);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Component[] convert(ComponentRow[] rows) {
		final List<Component> components = new ArrayList<>();
		for (ComponentRow row : rows)
			components.addAll(row.asComponents());
		return components.toArray(new Component[0]);
	}

	public static Class<?> getOptionClass(OptionType type) {
		switch (type) {
			case ROLE:
				return Role.class;
			case USER:
				return User.class;
			case CHANNEL:
				return MessageChannel.class;
			case NUMBER:
				return Number.class;
			case INTEGER:
				return Integer.class;
			case STRING:
				return String.class;
			case ATTACHMENT:
				return Message.Attachment.class;
			case BOOLEAN:
				return Boolean.class;
			case MENTIONABLE:
				return IMentionable.class;
			default:
				return Object.class;
		}
	}

	public static Object parseOptionValue(OptionMapping option) {
		try {
			switch (option.getType()) {
				case ROLE:
					return option.getAsRole();
				case USER:
					return option.getAsUser();
				case CHANNEL:
					return option.getAsChannel().asGuildMessageChannel();
				case NUMBER:
					return option.getAsDouble();
				case INTEGER:
					return option.getAsInt();
				case STRING:
					return option.getAsString();
				case ATTACHMENT:
					return option.getAsAttachment();
				case BOOLEAN:
					return option.getAsBoolean();
				case MENTIONABLE:
					return option.getAsMentionable();
				default:
					return null;
			}
		} catch (IllegalStateException ex) {
			return null;
		}
	}

	public static boolean isSnowflake(String snowflake) {
		return snowflake.length() <= 20 && Helpers.isNumeric(snowflake);
	}
}
