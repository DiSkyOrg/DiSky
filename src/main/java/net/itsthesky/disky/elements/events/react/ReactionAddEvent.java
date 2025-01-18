package net.itsthesky.disky.elements.events.react;

import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.itsthesky.disky.api.events.specific.MessageEvent;

public class ReactionAddEvent extends DiSkyEvent<MessageReactionAddEvent> {

	static {
		register("Reaction Add", ReactionAddEvent.class, ReactionAddEvent.BukkitReactionAddEvent.class,
				"(reaction|emote)[s] add[ed]")
				.description("Fired when a message, that can be seen by the bot, receive a reaction.",
						"This will be fired, by default, both guild & private messages, use the 'event is from guild' condition to avoid confusion.");

		SkriptUtils.registerBotValue(ReactionAddEvent.BukkitReactionAddEvent.class);

		SkriptUtils.registerRestValue("message",
				ReactionAddEvent.BukkitReactionAddEvent.class,
				event -> event.getJDAEvent().retrieveMessage());

		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild());
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, Member.class,
				event -> event.getJDAEvent().getMember());
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, User.class,
				event -> event.getJDAEvent().getUser());
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, MessageChannel.class,
				event -> event.getJDAEvent().getChannel());
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, Emote.class,
				event -> Emote.fromUnion(event.getJDAEvent().getEmoji()));
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, MessageReaction.class,
				event -> event.getJDAEvent().getReaction());
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, long.class,
				event -> event.getJDAEvent().getMessageAuthorIdLong());

		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, GuildChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, TextChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, NewsChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, ThreadChannel.class,
				event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

		SkriptUtils.registerValue(ReactionAddEvent.BukkitReactionAddEvent.class, PrivateChannel.class,
				event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
	}

	public static class BukkitReactionAddEvent extends SimpleDiSkyEvent<MessageReactionAddEvent> implements MessageEvent {
		public BukkitReactionAddEvent(ReactionAddEvent event) {}

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
