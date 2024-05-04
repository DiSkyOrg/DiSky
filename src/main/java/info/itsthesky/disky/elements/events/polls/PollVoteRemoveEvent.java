package info.itsthesky.disky.elements.events.polls;

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
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;

public class PollVoteRemoveEvent extends DiSkyEvent<MessagePollVoteRemoveEvent> {

    static {
        register("Poll Vote Remove", PollVoteRemoveEvent.class, BukkitPollVoteRemoveEvent.class,
                "[message] poll vote remove[d]");

        SkriptUtils.registerBotValue(BukkitPollVoteRemoveEvent.class);

        SkriptUtils.registerRestValue("message", BukkitPollVoteRemoveEvent.class, event -> event.getJDAEvent().retrieveMessage());
        SkriptUtils.registerRestValue("member", BukkitPollVoteRemoveEvent.class, event -> event.getJDAEvent().retrieveMember());
        SkriptUtils.registerRestValue("user", BukkitPollVoteRemoveEvent.class, event -> event.getJDAEvent().retrieveUser());

        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, GuildChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, TextChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asTextChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, NewsChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asNewsChannel() : null);
        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, ThreadChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asThreadChannel() : null);

        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, Guild.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuild() : null);
        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, MessageChannel.class,
                event -> event.getJDAEvent().getChannel());

        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, PrivateChannel.class,
                event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);

        SkriptUtils.registerValue(BukkitPollVoteRemoveEvent.class, Number.class,
                event -> event.getJDAEvent().getMessageIdLong());
    }

    public static class BukkitPollVoteRemoveEvent extends SimpleDiSkyEvent<MessagePollVoteRemoveEvent> {
        public BukkitPollVoteRemoveEvent(PollVoteRemoveEvent event) { }
    }
}
