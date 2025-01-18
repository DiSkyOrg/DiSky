package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedAuthor extends EmbedProperty<String> {

	static {
		register(EmbedAuthor.class,
				String.class,
				"author");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		builder.setAuthor(value);
	}

	@Override
	protected String convert(MessageEmbed embed) {
		return embed.getAuthor().getName();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
