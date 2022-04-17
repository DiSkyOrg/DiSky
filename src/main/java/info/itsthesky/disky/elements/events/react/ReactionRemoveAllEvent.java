package info.itsthesky.disky.elements.events.react;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;


public class ReactionRemoveAllEvent extends DiSkyEvent<MessageReactionRemoveAllEvent> {

	static {
		register("Reaction Remove All", ReactionRemoveAllEvent.class, BukkitReactionRemoveAllEvent.class,
				"(reaction|emote)[s] (remove[d] all|clear|reset)")
				.description("Fired when an user remove every reactions from a message.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.");

		SkriptUtils.registerBotValue(BukkitReactionRemoveAllEvent.class);

		SkriptUtils.registerRestValue("message",
				BukkitReactionRemoveAllEvent.class,
				event -> event.getJDAEvent().getChannel().retrieveMessageById(event.getJDAEvent().getMessageId()));

		SkriptUtils.registerAuthorValue(BukkitReactionRemoveAllEvent.class);

		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());

		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getTextChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getNewsChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getThreadChannel() : null);

		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getPrivateChannel() : null);
	}

	public static class BukkitReactionRemoveAllEvent extends SimpleDiSkyEvent<MessageReactionRemoveAllEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitReactionRemoveAllEvent(ReactionRemoveAllEvent event) {}

		@Override
		public GenericMessageEvent getMessageEvent() {
			return getJDAEvent();
		}
	}

}
