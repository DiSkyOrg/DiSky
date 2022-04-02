package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

public class EmbedFooter extends EmbedProperty<String> {

	static {
		register(EmbedFooter.class,
				String.class,
				"footer");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		builder.setFooter(value);
	}

	@Override
	protected String convert(MessageEmbed embed) {
		return embed.getFooter().getText();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
