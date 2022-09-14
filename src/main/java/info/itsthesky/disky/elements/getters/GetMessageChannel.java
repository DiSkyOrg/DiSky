package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Message Channel")
@Description({"This is an utility expression.",
		"It will returns a Message Channel (text, news or thread) out of the provided ID.",
		"This expression cannot be changed."})
@Examples("message channel with id \"000\"")
@Since("4.0.0")
public class GetMessageChannel extends BaseGetterExpression<GuildMessageChannel> {

	static {
		register(
				GetMessageChannel.class,
				GuildMessageChannel.class,
				"message channel"
		);
	}

	@Override
	protected GuildMessageChannel get(String id, Bot bot) {
		final GuildMessageChannel text = bot.getInstance().getTextChannelById(id);
		if (text != null)
			return text;

		final GuildMessageChannel news = bot.getInstance().getNewsChannelById(id);
		if (news != null)
			return news;

		final GuildMessageChannel thread = bot.getInstance().getThreadChannelById(id);
		if (thread != null)
			return thread;

		return null;
	}

	@Override
	public String getCodeName() {
		return "message channel";
	}

	@Override
	public @NotNull Class<? extends GuildMessageChannel> getReturnType() {
		return GuildMessageChannel.class;
	}
}
