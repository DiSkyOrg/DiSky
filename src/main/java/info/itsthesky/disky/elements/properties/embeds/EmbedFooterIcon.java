package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedFooterIcon extends EmbedProperty<String> {

	static {
		register(EmbedFooterIcon.class,
				String.class,
				"footer icon");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		if (builder.isEmpty())
			return;
		builder.setFooter(builder.build().getFooter().getText(), value);
	}

	@Override
	protected String convert(MessageEmbed embed) {
		return embed.getFooter().getIconUrl();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
