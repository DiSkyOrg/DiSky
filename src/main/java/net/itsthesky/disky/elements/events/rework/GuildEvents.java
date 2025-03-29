package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.update.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@EventCategory(name = "Guild Events", description = {
        "Events related to guilds (servers) on Discord.",
        "These events are triggered when certain actions occur within a guild, such as changes to settings, member actions, or administrative tasks."
})
public class GuildEvents {

    static {
        // Guild AFK Channel Event
        // Fired when a guild's AFK channel is updated
        EventRegistryFactory.builder(GuildUpdateAfkChannelEvent.class)
                .name("Guild AFK Channel Event")
                .patterns("[discord] guild afk channel (change|update)")
                .description("Fired when the AFK channel of a guild changes. Can be used to get the old/new channel, the author and the guild.")
                .example("on guild afk channel change:\n\tbroadcast \"Guild %event-guild% changed AFK channel from %past afk channel% to %current afk channel%\"")
                .customTimedExpressions("afk channel", VoiceChannel.class,
                        GuildUpdateAfkChannelEvent::getNewValue,
                        GuildUpdateAfkChannelEvent::getOldValue)
                .value(Guild.class, GuildUpdateAfkChannelEvent::getGuild, 0)
                .author(GuildUpdateAfkChannelEvent::getGuild)
                .register();

        // Guild AFK Timeout Event
        // Fired when a guild's AFK timeout duration is updated
        EventRegistryFactory.builder(GuildUpdateAfkTimeoutEvent.class)
                .name("Guild AFK Timeout Event")
                .patterns("[discord] guild afk timeout (change|update)")
                .description("Fired when the AFK timeout of a guild changes. Can be used to get the old/new timeout value, the author and the guild.")
                .example("on guild afk timeout change:\n\tbroadcast \"Guild %event-guild% changed AFK timeout from %past afk timeout% to %current afk timeout%\"")
                .customTimedExpressions("afk timeout", Guild.Timeout.class,
                        GuildUpdateAfkTimeoutEvent::getNewValue,
                        GuildUpdateAfkTimeoutEvent::getOldValue)
                .value(Guild.class, GuildUpdateAfkTimeoutEvent::getGuild, 0)
                .author(GuildUpdateAfkTimeoutEvent::getGuild)
                .register();

        // Guild AutoMod Execution Event
        // Fired when an automod rule is triggered and an automatic response is executed
        EventRegistryFactory.builder(AutoModExecutionEvent.class)
                .name("AutoMod Execution")
                .patterns("[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .example("on automod execute:\n\tbroadcast \"AutoMod rule triggered by %event-user% in %event-channel%\"")
                .value(Guild.class, AutoModExecutionEvent::getGuild, 0)
                .value(Channel.class, AutoModExecutionEvent::getChannel, 0)
                .value(AutoModResponse.class, AutoModExecutionEvent::getResponse, 0)
                .value(AutoModExecutionEvent.class, event -> event, 0)
                .value(String.class, AutoModExecutionEvent::getAlertMessageId, 0)
                .value(String.class, AutoModExecutionEvent::getRuleId, 0)
                .value(User.class, event -> event.getJDA().getUserById(event.getUserId()), 0)
                .register();

        // Guild Ban Event
        // Fired when a user is banned from a guild
        EventRegistryFactory.builder(GuildBanEvent.class)
                .name("Guild Ban Event")
                .patterns("[discord] guild [user] ban")
                .description("Fired when a user is banned from a guild. A member doesn't exist here because the member is not in the guild anymore! Can be used to get the banned user, the author and the guild.")
                .example("on guild ban:\n\tbroadcast \"%event-user% was banned from %event-guild%\"")
                .value(User.class, GuildBanEvent::getUser, 0)
                .value(Guild.class, GuildBanEvent::getGuild, 0)
                .author(GuildBanEvent::getGuild)
                .register();

        // Guild Banner Event
        // Fired when a guild's banner is updated
        EventRegistryFactory.builder(GuildUpdateBannerEvent.class)
                .name("Guild Banner Event")
                .patterns("[discord] guild banner (change|update)")
                .description("Fired when the banner of a guild changes. Can be used to get the old/new banner URL, the author and the guild.")
                .example("on guild banner change:\n\tbroadcast \"Guild %event-guild% changed banner from %past banner% to %current banner%\"")
                .customTimedExpressions("banner", String.class,
                        GuildUpdateBannerEvent::getNewBannerUrl,
                        GuildUpdateBannerEvent::getOldBannerUrl)
                .value(Guild.class, GuildUpdateBannerEvent::getGuild, 0)
                .author(GuildUpdateBannerEvent::getGuild)
                .register();

        // Guild Boost Count Event
        // Fired when a guild's boost count changes
        EventRegistryFactory.builder(GuildUpdateBoostCountEvent.class)
                .name("Guild Boost Count Update")
                .patterns("[discord] guild boost count (change|update)")
                .description("Fired when the boost count of a guild changes. Can be used to get the old/new count, and the guild.")
                .example("on guild boost count change:\n\tbroadcast \"Guild %event-guild% boost count changed from %past boost count% to %current boost count%\"")
                .customTimedExpressions("boost count", Integer.class,
                        GuildUpdateBoostCountEvent::getNewValue,
                        GuildUpdateBoostCountEvent::getOldValue)
                .value(Guild.class, GuildUpdateBoostCountEvent::getGuild, 0)
                .author(GuildUpdateBoostCountEvent::getGuild)
                .register();

        // Guild Boost Tier Event
        // Fired when a guild's boost tier level changes
        EventRegistryFactory.builder(GuildUpdateBoostTierEvent.class)
                .name("Guild Boost Tier Update")
                .patterns("[discord] guild boost tier (change|update)")
                .description("Fired when the boost tier of a guild changes. Can be used to get the old/new tier, and the guild.")
                .example("on guild boost tier change:\n\tbroadcast \"Guild %event-guild% boost tier changed from %past boost tier% to %current boost tier%\"")
                .customTimedExpressions("boost tier", String.class,
                        event -> event.getNewBoostTier().name(),
                        event -> event.getOldBoostTier().name())
                .value(Guild.class, GuildUpdateBoostTierEvent::getGuild, 0)
                .author(GuildUpdateBoostTierEvent::getGuild)
                .register();

        // Guild Icon Event
        // Fired when a guild's icon is updated
        EventRegistryFactory.builder(GuildUpdateIconEvent.class)
                .name("Guild Icon Event")
                .patterns("[discord] guild icon (change|update)")
                .description("Fired when the icon of a guild changes. Can be used to get the old/new icon URL, the author and the guild.")
                .example("on guild icon change:\n\tbroadcast \"Guild %event-guild% changed icon from %past icon% to %current icon%\"")
                .customTimedExpressions("icon", String.class,
                        GuildUpdateIconEvent::getNewIconUrl,
                        GuildUpdateIconEvent::getOldIconUrl)
                .value(Guild.class, GuildUpdateIconEvent::getGuild, 0)
                .author(GuildUpdateIconEvent::getGuild)
                .register();

        // Guild Invite Create Event
        // Fired when a new invite is created in a guild
        EventRegistryFactory.builder(GuildInviteCreateEvent.class)
                .name("Invite Create Event")
                .patterns("[discord] guild invite create")
                .description("Fired when an invite is created in a guild. Can be used to get the invite properties, the channel, the author and the guild.")
                .example("on guild invite create:\n\tbroadcast \"New invite created in %event-channel% with code %event-invite's code%\"")
                .value(Channel.class, GuildInviteCreateEvent::getChannel, 0)
                .value(Invite.class, GuildInviteCreateEvent::getInvite, 0)
                .value(Guild.class, GuildInviteCreateEvent::getGuild, 0)
                .author(GuildInviteCreateEvent::getGuild)
                .register();

        // Guild Invite Delete Event
        // Fired when an invite is deleted from a guild
        EventRegistryFactory.builder(GuildInviteDeleteEvent.class)
                .name("Invite Delete Event")
                .patterns("[discord] guild invite delete")
                .description("Fired when an invite is deleted from a guild. Can be used to get the invite code, the channel, the author and the guild.")
                .example("on guild invite delete:\n\tbroadcast \"Invite deleted from %event-channel% in %event-guild%\"")
                .value(Channel.class, GuildInviteDeleteEvent::getChannel, 0)
                .value(Guild.class, GuildInviteDeleteEvent::getGuild, 0)
                .author(GuildInviteDeleteEvent::getGuild)
                .register();

        // Guild Join Event
        // Fired when the bot joins a new guild
        EventRegistryFactory.builder(GuildJoinEvent.class)
                .name("Guild Join Event")
                .patterns("[discord] bot join guild")
                .description("Fired when the bot joins a guild. Use this to set up initial configurations or welcome messages.")
                .example("on bot join guild:\n\tbroadcast \"Bot joined %event-guild%!\"")
                .value(Guild.class, GuildJoinEvent::getGuild, 0)
                .register();

        // Guild Log Entry Event
        // Fired when a new audit log entry is created in a guild
        EventRegistryFactory.builder(GuildAuditLogEntryCreateEvent.class)
                .name("Guild Log Entry Create Event")
                .patterns("[discord] guild log [entry] create")
                .description("Fired when a new log entry is created in a guild. Can be used to monitor administrative actions within a guild.")
                .example("on guild log entry create:\n\tbroadcast \"New audit log entry created in %event-guild% for action type %event-entry's type%\"")
                .value(AuditLogEntry.class, GuildAuditLogEntryCreateEvent::getEntry, 0)
                .value(Guild.class, GuildAuditLogEntryCreateEvent::getGuild, 0)
                .author(GuildAuditLogEntryCreateEvent::getGuild)
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getEntry().getUserIdLong()))
                .register();

        // Guild Name Event
        // Fired when a guild's name is changed
        EventRegistryFactory.builder(GuildUpdateNameEvent.class)
                .name("Guild Name Event")
                .patterns("[discord] guild name (update|change)")
                .description("Fired when the name of a guild is changed. Can be used to get the old/new name, the author and the guild.")
                .example("on guild name change:\n\tbroadcast \"Guild name changed from '%past guild name%' to '%current guild name%'\"")
                .customTimedExpressions("guild name", String.class,
                        GuildUpdateNameEvent::getNewValue,
                        GuildUpdateNameEvent::getOldValue)
                .value(Guild.class, GuildUpdateNameEvent::getGuild, 0)
                .author(GuildUpdateNameEvent::getGuild)
                .register();

        // Guild Owner Event
        // Fired when a guild's owner changes
        EventRegistryFactory.builder(GuildUpdateOwnerEvent.class)
                .name("Guild Owner Event")
                .patterns("[discord] guild owner (change|update)")
                .description("Fired when the owner of a guild changes. Can be used to get the old/new owner, the author and the guild.")
                .example("on guild owner change:\n\tbroadcast \"Guild %event-guild% owner changed from %past owner% to %current owner%\"")
                .customTimedExpressions("owner", Member.class,
                        GuildUpdateOwnerEvent::getNewOwner,
                        GuildUpdateOwnerEvent::getOldOwner)
                .value(Guild.class, GuildUpdateOwnerEvent::getGuild)
                .author(GuildUpdateOwnerEvent::getGuild)
                .register();

        // Guild Splash Event
        // Fired when a guild's splash image is updated
        EventRegistryFactory.builder(GuildUpdateSplashEvent.class)
                .name("Guild Splash Event")
                .patterns("[discord] guild splash (change|update)")
                .description("Fired when the splash image of a guild changes. Can be used to get the old/new splash URL, the author and the guild.")
                .example("on guild splash change:\n\tbroadcast \"Guild %event-guild% splash changed from %past splash% to %current splash%\"")
                .customTimedExpressions("splash", String.class,
                        GuildUpdateSplashEvent::getNewSplashUrl,
                        GuildUpdateSplashEvent::getOldSplashUrl)
                .value(Guild.class, GuildUpdateSplashEvent::getGuild, 0)
                .author(GuildUpdateSplashEvent::getGuild)
                .register();

        // Guild Unban Event
        // Fired when a user is unbanned from a guild
        EventRegistryFactory.builder(GuildUnbanEvent.class)
                .name("Guild Unban Event")
                .patterns("[discord] guild [user] unban")
                .description("Fired when a user is unbanned from a guild. Can be used to get the unbanned user, the author and the guild.")
                .example("on guild unban:\n\tbroadcast \"%event-user% was unbanned from %event-guild%\"")
                .value(User.class, GuildUnbanEvent::getUser, 0)
                .value(Guild.class, GuildUnbanEvent::getGuild, 0)
                .author(GuildUnbanEvent::getGuild)
                .register();

        // Guild Voice Deafen Event
        // Fired when a member is deafened or undeafened by the guild
        EventRegistryFactory.builder(GuildVoiceGuildDeafenEvent.class)
                .name("Guild Voice Deafen Event")
                .patterns("[discord] guild [voice] deafen[ed]")
                .description("Fired when a member is deafened or undeafened by the guild. Can be used to track moderation actions in voice channels.")
                .example("on guild voice deafen:\n\tif event-boolean is true:\n\t\tbroadcast \"%event-member% was deafened in %event-guild%\"\n\telse:\n\t\tbroadcast \"%event-member% was undeafened in %event-guild%\"")
                .value(Boolean.class, GuildVoiceGuildDeafenEvent::isGuildDeafened, 0)
                .value(Member.class, GuildVoiceGuildDeafenEvent::getMember, 0)
                .value(Guild.class, GuildVoiceGuildDeafenEvent::getGuild, 0)
                .author(GuildVoiceGuildDeafenEvent::getGuild)
                .register();

        // Guild Voice Mute Event
        // Fired when a member is muted or unmuted by the guild
        EventRegistryFactory.builder(GuildVoiceGuildMuteEvent.class)
                .name("Guild Voice Mute Event")
                .patterns("[discord] guild [voice] mute[d]")
                .description("Fired when a member is muted or unmuted by the guild. Can be used to track moderation actions in voice channels.")
                .example("on guild voice mute:\n\tif event-boolean is true:\n\t\tbroadcast \"%event-member% was muted in %event-guild%\"\n\telse:\n\t\tbroadcast \"%event-member% was unmuted in %event-guild%\"")
                .value(Boolean.class, GuildVoiceGuildMuteEvent::isGuildMuted, 0)
                .value(Member.class, GuildVoiceGuildMuteEvent::getMember, 0)
                .value(Guild.class, GuildVoiceGuildMuteEvent::getGuild, 0)
                .author(GuildVoiceGuildMuteEvent::getGuild)
                .register();
    }
}