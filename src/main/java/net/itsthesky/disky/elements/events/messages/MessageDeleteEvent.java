package net.itsthesky.disky.elements.events.messages;

import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.managers.MessageManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.itsthesky.disky.api.events.specific.MessageEvent;

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

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, Message.class,
				event -> {
					DiSky.debug("Getting message from cache ["+ event.getJDAEvent().getJDA().getSelfUser().getId() +"]: " + event.getJDAEvent().getMessageIdLong());
					final MessageManager manager = MessageManager.getManager(event.getJDAEvent().getJDA());
					DiSky.debug(manager.getDeletedMessage(event.getJDAEvent().getMessageIdLong()) == null ? "Message is null" : ("Message is not null: " + manager.getDeletedMessage(event.getJDAEvent().getMessageIdLong()).getContentRaw()));
					return manager.getDeletedMessage(event.getJDAEvent().getMessageIdLong());
				});

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

		SkriptUtils.registerValue(BukkitMessageDeleteEvent.class, Number.class,
				event -> event.getJDAEvent().getMessageIdLong());
	}

	public static class BukkitMessageDeleteEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.message.MessageDeleteEvent> implements MessageEvent {
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