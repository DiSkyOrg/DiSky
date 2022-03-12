package info.itsthesky.disky.elements.embeds;

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
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		if (builder.isEmpty())
			return;
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
