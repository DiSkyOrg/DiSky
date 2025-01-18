package net.itsthesky.disky.elements.events.member;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.api.events.specific.MessageEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class MemberBoostEvent extends DiSkyEvent<MessageReceivedEvent> {

    @Override
    public boolean check(MessageReceivedEvent event) {
        return event.isFromGuild()
                && event.getMessage().getType().isSystem()
                && event.getMessage().getType().equals(MessageType.GUILD_MEMBER_BOOST);
    }

    static {
        /*register("Member Boost Event",
                MemberBanEvent.class, BukkitMemberBoostEvent.class,
                "[discord] member boost[ed]");*/

        SkriptUtils.registerBotValue(BukkitMemberBoostEvent.class);

        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, Message.class,
                event -> event.getJDAEvent().getMessage());
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, User.class,
                event -> event.getJDAEvent().getAuthor());
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, MessageChannel.class,
                event -> event.getJDAEvent().getChannel());

        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, GuildChannel.class,
                event -> event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getGuildChannel() : null);
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, TextChannel.class,
                event -> event.getJDAEvent().isFromType(ChannelType.TEXT) ? event.getJDAEvent().getChannel().asTextChannel() : null);
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, NewsChannel.class,
                event -> event.getJDAEvent().isFromType(ChannelType.NEWS) ? event.getJDAEvent().getChannel().asNewsChannel() : null);
        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, ThreadChannel.class,
                event -> event.getJDAEvent().isFromType(ChannelType.GUILD_PUBLIC_THREAD) || event.getJDAEvent().isFromType(ChannelType.GUILD_PRIVATE_THREAD) ? event.getJDAEvent().getChannel().asThreadChannel() : null);

        SkriptUtils.registerValue(BukkitMemberBoostEvent.class, PrivateChannel.class,
                event -> !event.getJDAEvent().isFromGuild() ? event.getJDAEvent().getChannel().asPrivateChannel() : null);
    }

    public static class BukkitMemberBoostEvent
            extends SimpleDiSkyEvent<MessageReceivedEvent>
            implements MessageEvent {

        public BukkitMemberBoostEvent(MemberBoostEvent event) { }

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
