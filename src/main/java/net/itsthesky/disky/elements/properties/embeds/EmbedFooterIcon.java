package net.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.Skript;
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
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		return new Class[] {String.class};
	}

	@Override
	public void set(EmbedBuilder builder, String value) {
		if (builder.isEmpty()) {
			Skript.error("You are trying to change a composed (author or footer) property of an empty embed. This is not possible.");
			return;
		}
		if (builder.build().getFooter() == null) {
			Skript.error("You are trying to set the footer of an embed that DO NOT have a footer yet.");
			return;
		}
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
