package info.itsthesky.disky.elements.events.messages;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.managers.MessageManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class MessageEditEvent extends DiSkyEvent<MessageUpdateEvent> {

	static {
		register("Message Edit", MessageEditEvent.class, BukkitMessageEditEvent.class,
				"message edit[ed]")
				.description("Fired when any message is edited / updated.",
						"Use 'event-string' to get the old message content, only works if this message was cached by DiSky before hand.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.");

		SkriptUtils.registerBotValue(BukkitMessageEditEvent.class);

		SkriptUtils.registerValue(BukkitMessageEditEvent.class, Guild.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuild() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		SkriptUtils.registerValue(BukkitMessageEditEvent.class, Message.class,
				event -> event.getJDAEvent().getMessage());
		SkriptUtils.registerValue(BukkitMessageEditEvent.class,
				String.class,
				event -> MessageManager.getManager(event.getJDAEvent().getJDA()).getEditedMessageOldContent(event.getJDAEvent().getMessageIdLong()));

		SkriptUtils.registerValue(BukkitMessageEditEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageEditEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitMessageEditEvent extends SimpleDiSkyEvent<MessageUpdateEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitMessageEditEvent(MessageEditEvent event) {}

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