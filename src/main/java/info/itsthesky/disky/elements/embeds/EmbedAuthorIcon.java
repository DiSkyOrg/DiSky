package info.itsthesky.disky.elements.embeds;

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedAuthorIcon extends EmbedProperty<String> {

	static {
		register(EmbedAuthorIcon.class,
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
				builder.build().getAuthor().getUrl(),
				value);
	}

	@Override
	protected String convert(MessageEmbed embed) {
		return embed.getAuthor().getIconUrl();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
