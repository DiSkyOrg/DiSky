package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogKey;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceSelfMuteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;

public class MemberEvents {

    static{
        // Member Join Event
        EventRegistryFactory.builder(GuildMemberJoinEvent.class)
                .name("Member Join Event")
                .patterns("member join[ed] [guild]")
                .description("Fired when a member joins a guild.")
                .example("on member join:")
                .value(Guild.class, GuildMemberJoinEvent::getGuild)
                .value(Member.class, GuildMemberJoinEvent::getMember)
                .register();

        // Member Remove Event
        EventRegistryFactory.builder(GuildMemberRemoveEvent.class)
                .name("Member Leave Event")
                .patterns("member (leave|left) [guild]")
                .description("Fired when a member is removed from a guild either by leaving or being punished. Use the ban/kick event instead to check the exact reason")
                .example("on member leave:")
                .value(Guild.class, GuildMemberRemoveEvent::getGuild)
                .value(Member.class, GuildMemberRemoveEvent::getMember)
                .register();

        // Member Role Add Event
        EventRegistryFactory.builder(GuildMemberRoleAddEvent.class)
                .name("Role Add Event")
                .patterns("[member] role add[ed]")
                .description("Fired when a member adds roles to another member, it's a log action so event-author returns who made the action event-roles returns a list of added roles")
                .example("on role add:")
                .value(Guild.class, GuildMemberRoleAddEvent::getGuild)
                .value(Member.class, GuildMemberRoleAddEvent::getMember)
                .value(Role[].class, event -> event.getRoles().toArray(new Role[0]), 0)
                .register();

        // Member Role Remove Event
        EventRegistryFactory.builder(GuildMemberRoleRemoveEvent.class)
                .name("Role Remove Event")
                .patterns("[member] role remove[d]")
                .description("Fired when a member removes roles from another member, it's a log action so event-author returns who made the action event-roles returns a list of removed roles")
                .example("on role remove:")
                .value(Guild.class, GuildMemberRoleRemoveEvent::getGuild)
                .value(Member.class, GuildMemberRoleRemoveEvent::getMember)
                .value(Role[].class, event -> event.getRoles().toArray(new Role[0]), 0)
                .register();

        // Member Nickname Event
        EventRegistryFactory.builder(GuildMemberUpdateNicknameEvent.class)
                .name("Member Nickname Event")
                .patterns("[guild] member nickname (change|update)")
                .description("Fired when a member changes their nickname.")
                .example("on member nickname change:")
                .value(String.class, GuildMemberUpdateNicknameEvent::getNewValue, 0)
                .value(String.class, GuildMemberUpdateNicknameEvent::getNewValue, 1)
                .value(String.class, GuildMemberUpdateNicknameEvent::getOldValue, -1)
                .value(Guild.class, GuildMemberUpdateNicknameEvent::getGuild)
                .value(Member.class, GuildMemberUpdateNicknameEvent::getMember)
                .register();

        // Member Avatar Event
        EventRegistryFactory.builder(GuildMemberUpdateAvatarEvent.class)
                .name("Member Avatar Event")
                .patterns("[guild] member avatar (change|update)")
                .description("Fired when a member changes their avatar.")
                .example("on member avatar change:")
                .value(String.class, GuildMemberUpdateAvatarEvent::getNewAvatarUrl, 0)
                .value(String.class, GuildMemberUpdateAvatarEvent::getNewAvatarUrl, 1)
                .value(String.class, GuildMemberUpdateAvatarEvent::getOldAvatarUrl, -1)
                .value(Guild.class, GuildMemberUpdateAvatarEvent::getGuild)
                .value(Member.class, GuildMemberUpdateAvatarEvent::getMember)
                .register();

        // Member Accept Screen Event
        EventRegistryFactory.builder(GuildMemberUpdatePendingEvent.class)
                .name("Member Accept Screen Event")
                .patterns("[guild] member screen accept")
                .description("Fired when a member has agreed to membership screen requirements it can be useful for adding roles since the member is not available if they haven't accepted it yet.")
                .example("on member screen accept:")
                .value(Boolean.class, GuildMemberUpdatePendingEvent::getNewValue, 0)
                .value(Boolean.class, GuildMemberUpdatePendingEvent::getNewValue, 1)
                .value(Boolean.class, GuildMemberUpdatePendingEvent::getOldValue, -1)
                .value(Guild.class, GuildMemberUpdatePendingEvent::getGuild)
                .value(Member.class, GuildMemberUpdatePendingEvent::getMember)
                .register();

        // Member Boost Time Update Event
        EventRegistryFactory.builder(GuildMemberUpdateBoostTimeEvent.class)
                .name("Member Boost Time Update Event")
                .patterns("[guild] member boost time (change|update)")
                .description("Fired when a member's boost time updates.")
                .example("on member boost time change:")
                .value(Date.class, event -> SkriptUtils.convertDateTime(event.getNewValue()), 0)
                .value(Date.class, event -> SkriptUtils.convertDateTime(event.getNewValue()), 1)
                .value(Date.class, event -> SkriptUtils.convertDateTime(event.getOldValue()), -1)
                .value(Guild.class, GuildMemberUpdateBoostTimeEvent::getGuild)
                .value(Member.class, GuildMemberUpdateBoostTimeEvent::getMember)
                .value(User.class, GuildMemberUpdateBoostTimeEvent::getUser)
                .register();

        // Member Boost Event
        EventRegistryFactory.builder(MessageReceivedEvent.class)
                .name("Member Boost Event")
                .patterns("member boost[ed]")
                .description("Fired when a member boosts a server.")
                .example("on member boost:")
                .implementMessage(MessageReceivedEvent::getChannel)
                .value(Message.class, MessageReceivedEvent::getMessage)
                .value(Guild.class, MessageReceivedEvent::getGuild)
                .value(User.class, MessageReceivedEvent::getAuthor)
                .channelValues(GenericMessageEvent::getChannel)
                .checker(event -> event.isFromGuild()
                        && event.getMessage().getType().isSystem() 
                        && event.getMessage().getType().equals(MessageType.GUILD_MEMBER_BOOST))
                .register();

        // Member Timeout Event
        EventRegistryFactory.builder(GuildMemberUpdateTimeOutEvent.class)
                .name("Member Timeout Event")
                .patterns("member time[ ]out[ed]")
                .description("Fired when a member is timed out.")
                .example("on member timeout:")
                .value(Guild.class, GuildMemberUpdateTimeOutEvent::getGuild)
                .value(Member.class, GuildMemberUpdateTimeOutEvent::getMember)
                .value(User.class, GuildMemberUpdateTimeOutEvent::getUser)
                .value(Date.class, event -> SkriptUtils.convertDateTime(event.getNewTimeOutEnd()), 0)
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getGuild().getIdLong()))
                .checker(event -> true)
                .logChecker(event -> event.getEntry().getChangeByKey(AuditLogKey.MEMBER_TIME_OUT) != null)
                .register();

        // Member Self Mute Event
        EventRegistryFactory.builder(GuildVoiceSelfMuteEvent.class)
                .name("Member Self Mute Event")
                .patterns("member [self] [un]mute[d]")
                .description("Fired when a member mutes or unmutes themselves")
                .example("on member mute:\n\tbroadcast event-boolean, event-member and event-guild")
                .value(Boolean.class, GuildVoiceSelfMuteEvent::isSelfMuted)
                .value(Guild.class, GuildVoiceSelfMuteEvent::getGuild)
                .value(Member.class, GuildVoiceSelfMuteEvent::getMember)
                .register();

        // Member Self Deafen Event
        EventRegistryFactory.builder(GuildVoiceSelfDeafenEvent.class)
                .name("Member Self Deafen Event")
                .patterns("member [self] [un]deafen[ed]")
                .description("Fired when a member deafens or undeafens themselves")
                .example("on member deafen:\n\tbroadcast event-boolean, event-member and event-guild")
                .value(Boolean.class, GuildVoiceSelfDeafenEvent::isSelfDeafened)
                .value(Guild.class, GuildVoiceSelfDeafenEvent::getGuild)
                .value(Member.class, GuildVoiceSelfDeafenEvent::getMember)
                .register();

        // Member Voice Join Event
        EventRegistryFactory.builder(GuildVoiceUpdateEvent.class)
                .name("Member Voice Join Event")
                .patterns("[member] voice [channel] join")
                .description("Fired when a member joins a voice or a stage channel, also fires when a member moves to another channel")
                .example("on voice channel join:")
                .value(AudioChannel.class, GuildVoiceUpdateEvent::getChannelJoined, 1)
                .value(AudioChannel.class, GuildVoiceUpdateEvent::getChannelJoined, 0)
                .value(AudioChannel.class, GuildVoiceUpdateEvent::getChannelLeft, -1)
                .value(VoiceChannel.class, event -> event.getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) event.getChannelJoined() : null, 1)
                .value(VoiceChannel.class, event -> event.getChannelJoined() instanceof VoiceChannel ? (VoiceChannel) event.getChannelJoined() : null, 0)
                .value(VoiceChannel.class, event -> event.getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) event.getChannelLeft() : null, -1)
                .value(StageChannel.class, event -> event.getChannelJoined() instanceof StageChannel ? (StageChannel) event.getChannelJoined() : null, 1)
                .value(StageChannel.class, event -> event.getChannelJoined() instanceof StageChannel ? (StageChannel) event.getChannelJoined() : null, 0)
                .value(StageChannel.class, event -> event.getChannelLeft() instanceof StageChannel ? (StageChannel) event.getChannelLeft() : null, -1)
                .value(Guild.class, GuildVoiceUpdateEvent::getGuild)
                .value(Member.class, GuildVoiceUpdateEvent::getMember)
                .checker(event -> event.getChannelJoined() != null)
                .register();

        // Member Voice Leave Event
        EventRegistryFactory.builder(GuildVoiceUpdateEvent.class)
                .name("Member Voice Leave Event")
                .patterns("[member] voice [channel] leave")
                .description("Fired when a member leaves a voice or a stage channel")
                .example("on voice channel leave:")
                .value(AudioChannel.class, GuildVoiceUpdateEvent::getChannelLeft)
                .value(VoiceChannel.class, event -> event.getChannelLeft() instanceof VoiceChannel ? (VoiceChannel) event.getChannelLeft() : null)
                .value(StageChannel.class, event -> event.getChannelLeft() instanceof StageChannel ? (StageChannel) event.getChannelLeft() : null)
                .value(Guild.class, GuildVoiceUpdateEvent::getGuild)
                .value(Member.class, GuildVoiceUpdateEvent::getMember)
                .checker(event -> event.getChannelLeft() != null)
                .register();
    }
}