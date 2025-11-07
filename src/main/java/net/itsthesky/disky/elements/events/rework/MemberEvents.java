package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.itsthesky.disky.api.events.rework.CopyEventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;
import java.util.Set;

@CopyEventCategory(UserEvents.class)
public class MemberEvents {

    static {
        // Member Join Event
        EventRegistryFactory.builder(GuildMemberJoinEvent.class)
                .name("Member Join Event")
                .patterns("member join[ed] [guild]")
                .description("Fired when a member joins a guild.")
                .example("on member join:\n    broadcast \"Welcome %event-member% to %event-guild%!\"")
                .value(Guild.class, GuildMemberJoinEvent::getGuild)
                .value(Member.class, GuildMemberJoinEvent::getMember)
                .author(GuildMemberJoinEvent::getGuild)
                .register();

        // Member Remove Event
        EventRegistryFactory.builder(GuildMemberRemoveEvent.class)
                .name("Member Leave Event")
                .patterns("member (leave|left) [guild]")
                .description("Fired when a member is removed from a guild either by leaving or being punished. Use the ban/kick event instead to check the exact reason.")
                .example("on member leave:\n    broadcast \"%event-member% has left %event-guild%\"")
                .value(Guild.class, GuildMemberRemoveEvent::getGuild)
                .value(Member.class, GuildMemberRemoveEvent::getMember)
                .value(User.class, GuildMemberRemoveEvent::getUser)
                .author(GuildMemberRemoveEvent::getGuild)
                .register();

        // Member Role Add Event
        EventRegistryFactory.builder(GuildMemberRoleAddEvent.class)
                .name("Role Add Event")
                .patterns("[member] role add[ed]")
                .description("Fired when a member receives new roles. This is a log action, so event-author returns who made the action and event-roles returns a list of added roles.")
                .example("on role add:\n    broadcast \"%event-author% added roles %added roles% to %event-member%\"")
                .listExpression("added roles", Role.class,
                        evt -> evt.getRoles().toArray(Role[]::new))
                .value(Guild.class, GuildMemberRoleAddEvent::getGuild)
                .value(Member.class, GuildMemberRoleAddEvent::getMember)
                .author(GuildMemberRoleAddEvent::getGuild)
                .register();

        // Member Role Remove Event
        EventRegistryFactory.builder(GuildMemberRoleRemoveEvent.class)
                .name("Role Remove Event")
                .patterns("[member] role remove[d]")
                .description("Fired when roles are removed from a member. This is a log action, so event-author returns who made the action and event-roles returns a list of removed roles.")
                .example("on role remove:\n    broadcast \"%event-author% removed roles %removed roles% from %event-member%\"")
                .listExpression("removed roles", Role.class,
                        evt -> evt.getRoles().toArray(Role[]::new))
                .value(Guild.class, GuildMemberRoleRemoveEvent::getGuild)
                .value(Member.class, GuildMemberRoleRemoveEvent::getMember)
                .author(GuildMemberRoleRemoveEvent::getGuild)
                .register();

        // Member Nickname Event
        EventRegistryFactory.builder(GuildMemberUpdateNicknameEvent.class)
                .name("Member Nickname Event")
                .patterns("[guild] member nickname (change|update)")
                .description("Fired when a member changes their nickname in a guild.")
                .example("on member nickname change:\n    broadcast \"%event-member% changed their nickname from '%previous nickname%' to '%current nickname%'\"")
                .customTimedExpressions("[member] nickname", String.class,
                        GuildMemberUpdateNicknameEvent::getNewValue,
                        GuildMemberUpdateNicknameEvent::getOldValue)
                .value(Guild.class, GuildMemberUpdateNicknameEvent::getGuild)
                .value(Member.class, GuildMemberUpdateNicknameEvent::getMember)
                .author(GuildMemberUpdateNicknameEvent::getGuild)
                .register();

        // Member Avatar Event
        EventRegistryFactory.builder(GuildMemberUpdateAvatarEvent.class)
                .name("Member Avatar Event")
                .patterns("[guild] member avatar (change|update)")
                .description("Fired when a member changes their server-specific avatar.")
                .example("on member avatar change:\n    broadcast \"%event-member% changed their server avatar. New URL: %current avatar url%\"")
                .customTimedExpressions("[member] avatar url", String.class,
                        GuildMemberUpdateAvatarEvent::getNewAvatarUrl,
                        GuildMemberUpdateAvatarEvent::getOldAvatarUrl)
                .value(Guild.class, GuildMemberUpdateAvatarEvent::getGuild)
                .value(Member.class, GuildMemberUpdateAvatarEvent::getMember)
                .author(GuildMemberUpdateAvatarEvent::getGuild)
                .register();

        // Member Accept Screen Event
        EventRegistryFactory.builder(GuildMemberUpdatePendingEvent.class)
                .name("Member Accept Screen Event")
                .patterns("[guild] member screen accept")
                .description("Fired when a member has agreed to membership screen requirements. This can be useful for adding roles since the member is not fully available until they've accepted the screen requirements.")
                .example("on member screen accept:\n    broadcast \"%event-member% has completed the membership screening in %event-guild%\"")
                .customTimedExpressions("[member] pending state", Boolean.class,
                        GuildMemberUpdatePendingEvent::getNewValue,
                        GuildMemberUpdatePendingEvent::getOldValue)
                .value(Guild.class, GuildMemberUpdatePendingEvent::getGuild)
                .value(Member.class, GuildMemberUpdatePendingEvent::getMember)
                .author(GuildMemberUpdatePendingEvent::getGuild)
                .register();

        // Member Boost Time Update Event
        EventRegistryFactory.builder(GuildMemberUpdateBoostTimeEvent.class)
                .name("Member Boost Time Update Event")
                .patterns("[guild] member boost time (change|update)")
                .description("Fired when a member's boost time is updated, which can happen when they start or stop boosting a server.")
                .example("on member boost time change:\n    broadcast \"%event-member% boost time updated from %previous boost time% to %current boost time%\"")
                .customTimedExpressions("[member] boost time", Date.class,
                        evt -> SkriptUtils.convertDateTime(evt.getNewValue()),
                        evt -> SkriptUtils.convertDateTime(evt.getOldValue()))
                .value(Guild.class, GuildMemberUpdateBoostTimeEvent::getGuild)
                .value(Member.class, GuildMemberUpdateBoostTimeEvent::getMember)
                .value(User.class, GuildMemberUpdateBoostTimeEvent::getUser)
                .author(GuildMemberUpdateBoostTimeEvent::getGuild)
                .register();

        // Member Boost Event
        final Set<MessageType> BOOST_MESSAGE_TYPES = Set.of(
                MessageType.GUILD_MEMBER_BOOST,
                MessageType.GUILD_BOOST_TIER_1,
                MessageType.GUILD_BOOST_TIER_2,
                MessageType.GUILD_BOOST_TIER_3
        );
        EventRegistryFactory.builder(MessageReceivedEvent.class)
                .name("Member Boost Event")
                .patterns("member boost[ed]")
                .description("Fired when a member boosts a server, which is detected through a system message in the server.")
                .example("on member boost:\n    broadcast \"Thank you %event-user% for boosting %event-guild%!\"")
                .implementMessage(MessageReceivedEvent::getChannel)
                .value(Message.class, MessageReceivedEvent::getMessage)
                .value(Guild.class, MessageReceivedEvent::getGuild)
                .value(User.class, MessageReceivedEvent::getAuthor)
                .channelValues(GenericMessageEvent::getChannel)
                .author(MessageReceivedEvent::getGuild)
                .checker(event -> {
                    if (!event.isFromGuild())
                        return false;
                    MessageType type = event.getMessage().getType();
                    if (!type.isSystem())
                        return false;
                    return Set.of(
                            MessageType.GUILD_MEMBER_BOOST,
                            MessageType.GUILD_BOOST_TIER_1,
                            MessageType.GUILD_BOOST_TIER_2,
                            MessageType.GUILD_BOOST_TIER_3
                    ).contains(type);
                })
                .register();

        // Member Timeout Event
        EventRegistryFactory.builder(GuildMemberUpdateTimeOutEvent.class)
                .name("Member Timeout Event")
                .patterns("member time[ ]out[ed]")
                .description("Fired when a member is timed out (temporarily restricted from interacting with the server).")
                .example("on member timeout:\n    broadcast \"%event-member% has been timed out until %timeout end%.\"")
                .customTimedExpressions("[member] timeout end", Date.class,
                        evt -> SkriptUtils.convertDateTime(evt.getNewTimeOutEnd()),
                        evt -> SkriptUtils.convertDateTime(evt.getOldTimeOutEnd()))
                .value(Guild.class, GuildMemberUpdateTimeOutEvent::getGuild)
                .value(Member.class, GuildMemberUpdateTimeOutEvent::getMember)
                .value(User.class, GuildMemberUpdateTimeOutEvent::getUser)
                .author(GuildMemberUpdateTimeOutEvent::getGuild)
                .logChecker(event -> event.getEntry().getChangeByKey(AuditLogKey.MEMBER_TIME_OUT) != null)
                .register();

        // Member Self Mute Event
        EventRegistryFactory.builder(GuildVoiceSelfMuteEvent.class)
                .name("Member Self Mute Event")
                .patterns("member [self] [un]mute[d]")
                .description("Fired when a member mutes or unmutes themselves in a voice channel.")
                .example("on member mute:\n    if member mute state is true:\n        broadcast \"%event-member% muted themselves\"\n    else:\n        broadcast \"%event-member% unmuted themselves\"")
                .customTimedExpressions("[member] mute[d] state", Boolean.class,
                        GuildVoiceSelfMuteEvent::isSelfMuted,
                        evt -> !evt.isSelfMuted())
                .value(Guild.class, GuildVoiceSelfMuteEvent::getGuild)
                .value(Member.class, GuildVoiceSelfMuteEvent::getMember)
                .author(GuildVoiceSelfMuteEvent::getGuild)
                .register();

        // Member Self Deafen Event
        EventRegistryFactory.builder(GuildVoiceSelfDeafenEvent.class)
                .name("Member Self Deafen Event")
                .patterns("member [self] [un]deafen[ed]")
                .description("Fired when a member deafens or undeafens themselves in a voice channel.")
                .example("on member deafen:\n    if member deafen state is true:\n        broadcast \"%event-member% deafened themselves\"\n    else:\n        broadcast \"%event-member% undeafened themselves\"")
                .customTimedExpressions("[member] deafen[ed] state", Boolean.class,
                        GuildVoiceSelfDeafenEvent::isSelfDeafened,
                        evt -> !evt.isSelfDeafened())
                .value(Guild.class, GuildVoiceSelfDeafenEvent::getGuild)
                .value(Member.class, GuildVoiceSelfDeafenEvent::getMember)
                .author(GuildVoiceSelfDeafenEvent::getGuild)
                .register();

        // Member Voice Join Event
        EventRegistryFactory.builder(GuildVoiceUpdateEvent.class)
                .name("Member Voice Join Event")
                .patterns("[member] voice [channel] join")
                .description("Fired when a member joins a voice or stage channel. This event also fires when a member moves from one voice channel to another.")
                .example("on voice channel join:\n    broadcast \"%event-member% joined voice channel %joined voice channel%\"")
                .customTimedExpressions("[joined] voice channel", AudioChannel.class,
                        GuildVoiceUpdateEvent::getChannelJoined,
                        GuildVoiceUpdateEvent::getChannelLeft)
                .customTimedExpressions("[joined] voice", VoiceChannel.class,
                        evt -> evt.getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) evt.getChannelJoined() : null,
                        evt -> evt.getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) evt.getChannelLeft() : null)
                .customTimedExpressions("[joined] stage", StageChannel.class,
                        evt -> evt.getChannelJoined() instanceof StageChannel ? (StageChannel) evt.getChannelJoined() : null,
                        evt -> evt.getChannelLeft() instanceof StageChannel ? (StageChannel) evt.getChannelLeft() : null)
                .value(Guild.class, GuildVoiceUpdateEvent::getGuild)
                .value(Member.class, GuildVoiceUpdateEvent::getMember)
                .author(GuildVoiceUpdateEvent::getGuild)
                .checker(event -> event.getChannelJoined() != null)
                .register();

        // Member Voice Leave Event
        EventRegistryFactory.builder(GuildVoiceUpdateEvent.class)
                .name("Member Voice Leave Event")
                .patterns("[member] voice [channel] leave")
                .description("Fired when a member leaves a voice or stage channel. This includes both disconnecting completely and moving to another channel.")
                .example("on voice channel leave:\n    broadcast \"%event-member% left voice channel %left voice channel%\"")
                .customTimedExpressions("[left] voice channel", AudioChannel.class,
                        GuildVoiceUpdateEvent::getChannelLeft,
                        GuildVoiceUpdateEvent::getChannelJoined)
                .customTimedExpressions("[left] voice", VoiceChannel.class,
                        evt -> evt.getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) evt.getChannelLeft() : null,
                        evt -> evt.getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) evt.getChannelJoined() : null)
                .customTimedExpressions("[left] stage", StageChannel.class,
                        evt -> evt.getChannelLeft() instanceof StageChannel ? (StageChannel) evt.getChannelLeft() : null,
                        evt -> evt.getChannelJoined() instanceof StageChannel ? (StageChannel) evt.getChannelJoined() : null)
                .value(Guild.class, GuildVoiceUpdateEvent::getGuild)
                .value(Member.class, GuildVoiceUpdateEvent::getMember)
                .author(GuildVoiceUpdateEvent::getGuild)
                .checker(event -> event.getChannelLeft() != null)
                .register();
    }
}