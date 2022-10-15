package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedAuthorURL extends EmbedProperty<String> {

	static {
		register(EmbedAuthorURL.class,
				String.class,
				"author url");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		if (builder.isEmpty()) {
			Skript.error("You are trying to change a composed (author or footer) property of an empty embed. This is not possible.");
			return;
		}
		if (builder.build().getAuthor() == null) {
			Skript.error("You are trying to set the author of an embed that DO NOT have an author yet.");
			return;
		}
		builder.setAuthor(builder.build().getAuthor().getName(),
				value,
				builder.build().getAuthor().getIconUrl());
	}

	@Override
	protected String convert(MessageEmbed embed) {
		return embed.getAuthor().getUrl();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
