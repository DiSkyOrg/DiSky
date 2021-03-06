package info.itsthesky.disky.elements.events.messages;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.managers.MessageManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
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
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitMessageEditEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitMessageEditEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitMessageEditEvent extends SimpleDiSkyEvent<MessageUpdateEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitMessageEditEvent(MessageEditEvent event) {}

		@Override
		public GenericMessageEvent getMessageEvent() {
			return getJDAEvent();
		}
	}
}