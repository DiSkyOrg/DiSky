package info.itsthesky.disky.elements.events.messages;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.managers.MessageManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;

public class MessageDeleteEvent extends DiSkyEvent<net.dv8tion.jda.api.events.message.MessageDeleteEvent> {

	static {
		register("Message Delete", MessageDeleteEvent.class, BukkitMessageDeleteEvent.class,
				"message delete[d]")
				.description("Fired when any message is deleted.",
						"Use 'event-string' to get the old message content, only works if this message was cached by DiSky before hand.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.");

		SkriptUtils.registerBotValue(BukkitMessageDeleteEvent.class);
		SkriptUtils.registerAuthorValue(BukkitMessageDeleteEvent.class);

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, Guild.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuild() : null);
		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class,
				String.class,
				event -> MessageManager.getManager(event.getJDAEvent().getJDA()).getDeletedMessageContent(event.getJDAEvent().getMessageIdLong()));

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitMessageDeleteEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.message.MessageDeleteEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitMessageDeleteEvent(MessageDeleteEvent event) {}

		@Override
		public MessageChannel getMessageChannel() {
			return getJDAEvent().getChannel();
		}

		@Override
		public boolean isFromGuild() {
			return getJDAEvent().isFromGuild();
		}
	}
}