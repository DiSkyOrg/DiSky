package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.Channel;

@Name("Get Channel")
@Description({"A generic expression to get any channel from its ID.",
"This can return a text, private, news, voice, category, stage, thread or post channel."})
@Examples("post last embed to channel with id \"000\"")
@Since("4.4.2")
public class GetChannel extends BaseGetterExpression<Channel> {

	static {
		register(
				GetChannel.class,
				Channel.class,
				"channel"
		);
	}

	@Override
	protected Channel get(String id, Bot bot) {
		Channel channel;

		channel = bot.getInstance().getTextChannelById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getVoiceChannelById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getCategoryById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getStageChannelById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getNewsChannelById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getPrivateChannelById(id);
		if (channel != null) return channel;

		channel = bot.getInstance().getThreadChannelById(id);
		return channel;
	}

	@Override
	public String getCodeName() {
		return "channel";
	}

	@Override
	public Class<? extends Channel> getReturnType() {
		return Channel.class;
	}

}
