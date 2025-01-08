package info.itsthesky.disky.elements.events.react;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;


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
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitReactionRemoveAllEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitReactionRemoveAllEvent extends SimpleDiSkyEvent<MessageReactionRemoveAllEvent> implements info.itsthesky.disky.api.events.specific.MessageEvent {
		public BukkitReactionRemoveAllEvent(ReactionRemoveAllEvent event) {}

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
