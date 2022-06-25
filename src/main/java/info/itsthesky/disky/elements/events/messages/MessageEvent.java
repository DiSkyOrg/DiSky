package info.itsthesky.disky.elements.events.messages;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MessageEvent extends DiSkyEvent<MessageReceivedEvent> {

	static {
		register("Message Receive", MessageEvent.class, BukkitMessageEvent.class,
				"message receive[d]")
				.description("Fired when any bot receive an actual message.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.")
				.examples("on message received:",
						"\tif message is from guild:",
						"\t\treply with \"I just received '%event-message%' from %mention tag of event-channel%!\"",
						"\telse:",
						"\t\treply with \"I just received '%event-message%' from %mention tag of event-user%!\"");

		SkriptUtils.registerBotValue(BukkitMessageEvent.class);

		SkriptUtils.registerValue(BukkitMessageEvent.class, Message.class,
				event -> event.getJDAEvent().getMessage());
		SkriptUtils.registerValue(BukkitMessageEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitMessageEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitMessageEvent.class, User.class,
				event -> event.getJDAEvent().getAuthor());
		SkriptUtils.registerValue(BukkitMessageEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		for (int i = 0; i < 10; i++) {
			final ChannelType type = ChannelType.values()[i];
		}

		SkriptUtils.registerValue(BukkitMessageEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromType(ChannelType.TEXT) ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromType(ChannelType.NEWS) ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromType(ChannelType.GUILD_PUBLIC_THREAD) || event.getJDAEvent().isFromType(ChannelType.GUILD_PRIVATE_THREAD) ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitMessageEvent extends SimpleDiSkyEvent<MessageReceivedEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitMessageEvent(MessageEvent event) {}

		@Override
		public GenericMessageEvent getMessageEvent() {
			return getJDAEvent();
		}
	}
}