package net.itsthesky.disky.elements.events.polls;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;

public class PollVoteAddEvent extends DiSkyEvent<MessagePollVoteAddEvent> {

    static {
        register("Poll Vote Add", PollVoteAddEvent.class, BukkitPollVoteAddEvent.class,
                "[message] poll vote add[ed]");

        SkriptUtils.registerBotValue(BukkitPollVoteAddEvent.class);

        SkriptUtils.registerRestValue("message", BukkitPollVoteAddEvent.class, event -> event.getJDAEvent().retrieveMessage());
        SkriptUtils.registerRestValue("member", BukkitPollVoteAddEvent.class, event -> event.getJDAEvent().retrieveMember());
        SkriptUtils.registerRestValue("user", BukkitPollVoteAddEvent.class, event -> event.getJDAEvent().retrieveUser());

        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, GuildChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, TextChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, NewsChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, ThreadChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, Guild.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuild() : null);
        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, MessageChannel.class,
                event -> event.getJDAEvent().getChannel());

        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, PrivateChannel.class,
                event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);

        SkriptUtils.registerValue(BukkitPollVoteAddEvent.class, Number.class,
                event -> event.getJDAEvent().getMessageIdLong());
    }

    public static class BukkitPollVoteAddEvent extends SimpleDiSkyEvent<MessagePollVoteAddEvent> {
        public BukkitPollVoteAddEvent(PollVoteAddEvent event) { }
    }
}
