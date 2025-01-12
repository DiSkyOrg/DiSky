package info.itsthesky.disky.elements.properties.embeds;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class EmbedTimeStamp extends EmbedProperty<Date> {

	static {
		register(EmbedTimeStamp.class,
				Date.class,
				"time[( |-)]stamp");
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		return new Class[] {Date.class};
	}

	@Override
	public void set(EmbedBuilder builder, Date value) {
		builder.setTimestamp(Instant.ofEpochMilli(value.getTime()));
	}

	@Override
	protected Date convert(MessageEmbed embed) {
		return new Date(embed.getTimestamp().getSecond() / 1000);
	}

	@Override
	public @NotNull Class<? extends Date> getReturnType() {
		return Date.class;
	}

}
