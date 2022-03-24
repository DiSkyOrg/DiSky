package info.itsthesky.disky.elements.events.react;

import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

public class ReactionRemoveEvent extends DiSkyEvent<MessageReactionRemoveEvent> {

	static {
		register("Reaction Remove", ReactionRemoveEvent.class, BukkitReactionRemoveEvent.class,
				"(reaction|emote)[s] add[ed]")
				.description("Fired when a message, that can be seen by the bot, receive a reaction.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.");

		SkriptUtils.registerBotValue(BukkitReactionRemoveEvent.class);

		SkriptUtils.registerRestValue("message",
				BukkitReactionRemoveEvent.class,
				event -> event.getJDAEvent().retrieveMessage());

		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, Emote.class,
				event -> Emote.fromReaction(event.getJDAEvent().getReactionEmote()));

		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitReactionRemoveEvent extends SimpleDiSkyEvent<MessageReactionRemoveEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitReactionRemoveEvent(ReactionRemoveEvent event) {}

		@Override
		public GenericMessageEvent getMessageEvent() {
			return getJDAEvent();
		}
	}
	
}
