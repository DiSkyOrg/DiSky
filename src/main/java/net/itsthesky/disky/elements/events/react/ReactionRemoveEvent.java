package net.itsthesky.disky.elements.events.react;

import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.itsthesky.disky.api.events.specific.MessageEvent;

public class ReactionRemoveEvent extends DiSkyEvent<MessageReactionRemoveEvent> {

	static {
		register("Reaction Remove", ReactionRemoveEvent.class, BukkitReactionRemoveEvent.class,
				"(reaction|emote)[s] remove[d]")
				.description("Fired when an user remove a reaction from a specific message.",
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
				event -> Emote.fromUnion(event.getJDAEvent().getEmoji()));

		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(BukkitReactionRemoveEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitReactionRemoveEvent extends SimpleDiSkyEvent<MessageReactionRemoveEvent> implements MessageEvent {
		public BukkitReactionRemoveEvent(ReactionRemoveEvent event) {}

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
