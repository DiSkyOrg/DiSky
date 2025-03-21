package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.audit.AuditLogKey;
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
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkChannelEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBannerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostCountEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateBoostTierEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateSplashEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceGuildMuteEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.api.events.specific.LogEvent;
import net.itsthesky.disky.core.SkriptUtils;

public class GuildEvents {

    static {
        // Guild AFK Channel Event
        EventRegistryFactory.builder(GuildUpdateAfkChannelEvent.class)
                .name("Guild AFK Channel Event")
                .patterns("[discord] guild afk channel (change|update)")
                .description("Fired when a afk channel of a guild changes can be used to get the old/new channel, the author and the guild.")
                .example("on guild afk channel change:")
                .value(VoiceChannel.class, GuildUpdateAfkChannelEvent::getOldValue, -1)
                .value(VoiceChannel.class, GuildUpdateAfkChannelEvent::getNewValue, 0)
                .value(VoiceChannel.class, GuildUpdateAfkChannelEvent::getNewValue, 1)
                .value(Guild.class, GuildUpdateAfkChannelEvent::getGuild, 0)
                .author(GuildUpdateAfkChannelEvent::getGuild)
                .register();

        // Guild AFK Timeout Event
        EventRegistryFactory.builder(GuildUpdateAfkTimeoutEvent.class)
                .name("Guild AFK Timeout Event")
                .patterns("[discord] guild afk timeout (change|update)")
                .description("Fired when a afk timeout of a guild changes can be used to get the old/new timeout value, the author and the guild.")
                .example("on guild afk timeout change:")
                .value(Guild.Timeout.class, GuildUpdateAfkTimeoutEvent::getOldValue, -1)
                .value(Guild.Timeout.class, GuildUpdateAfkTimeoutEvent::getNewValue, 0)
                .value(Guild.Timeout.class, GuildUpdateAfkTimeoutEvent::getNewValue, 1)
                .value(Guild.class, GuildUpdateAfkTimeoutEvent::getGuild, 0)
                .author(GuildUpdateAfkTimeoutEvent::getGuild)
                .register();

        // Guild AutoMod Execution Event
        EventRegistryFactory.builder(AutoModExecutionEvent.class)
                .name("AutoMod Execution")
                .patterns("[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .example("on automod execute:")
                .value(Guild.class, AutoModExecutionEvent::getGuild, 0)
                .value(Channel.class, AutoModExecutionEvent::getChannel, 0)
                .value(AutoModResponse.class, AutoModExecutionEvent::getResponse, 0)
                .value(AutoModExecutionEvent.class, event -> event, 0)
                .value(String.class, AutoModExecutionEvent::getAlertMessageId, 0)
                .value(String.class, AutoModExecutionEvent::getRuleId, 0)
                .value(User.class, event -> event.getJDA().getUserById(event.getUserId()), 0)
                .register();

        // Guild Ban Event
        EventRegistryFactory.builder(GuildBanEvent.class)
                .name("Guild Ban Event")
                .patterns("[discord] guild [user] ban")
                .description("Fired when a user is banned from a guild. A member doesn't exist here because the member is not in the guild anymore! Can be used to get the banned user, the author and the guild.")
                .example("on guild ban:")
                .value(User.class, GuildBanEvent::getUser, 0)
                .value(Guild.class, GuildBanEvent::getGuild, 0)
                .author(GuildBanEvent::getGuild)
                .register();

        // Guild Banner Event
        EventRegistryFactory.builder(GuildUpdateBannerEvent.class)
                .name("Guild Banner Event")
                .patterns("[discord] guild banner (change|update)")
                .description("Fired when a banner of a guild changes can be used to get the old/new banner, the author and the guild.")
                .example("on guild banner change:")
                .value(String.class, GuildUpdateBannerEvent::getOldBannerUrl, -1)
                .value(String.class, GuildUpdateBannerEvent::getNewBannerUrl, 0)
                .value(String.class, GuildUpdateBannerEvent::getNewBannerUrl, 1)
                .value(Guild.class, GuildUpdateBannerEvent::getGuild, 0)
                .author(GuildUpdateBannerEvent::getGuild)
                .register();

        // Guild Boost Count Event
        EventRegistryFactory.builder(GuildUpdateBoostCountEvent.class)
                .name("Guild Boost Count Update")
                .patterns("[discord] guild boost count (change|update)")
                .description("Fired when a boost count of a guild changes - can be used to get the old/new count, and the guild.")
                .example("on guild boost count change:")
                .value(Integer.class, GuildUpdateBoostCountEvent::getOldValue, -1)
                .value(Integer.class, GuildUpdateBoostCountEvent::getNewValue, 0)
                .value(Integer.class, GuildUpdateBoostCountEvent::getNewValue, 1)
                .value(Guild.class, GuildUpdateBoostCountEvent::getGuild, 0)
                .author(GuildUpdateBoostCountEvent::getGuild)
                .register();

        // Guild Boost Tier Event
        EventRegistryFactory.builder(GuildUpdateBoostTierEvent.class)
                .name("Guild Boost Tier Update")
                .patterns("[discord] guild boost tier (change|update)")
                .description("Fired when a boost tier of a guild changes - can be used to get the old/new tier, and the guild.")
                .example("on guild boost tier change:")
                .value(String.class, event -> event.getOldBoostTier().name(), -1)
                .value(String.class, event -> event.getNewBoostTier().name(), 0)
                .value(String.class, event -> event.getNewBoostTier().name(), 1)
                .value(Guild.class, GuildUpdateBoostTierEvent::getGuild, 0)
                .author(GuildUpdateBoostTierEvent::getGuild)
                .register();

        // Guild Icon Event
        EventRegistryFactory.builder(GuildUpdateIconEvent.class)
                .name("Guild Icon Event")
                .patterns("[discord] guild icon (change|update)")
                .description("Fired when the icon of a guild changes can be used to get the old/new icon, the author and the guild.")
                .example("on guild icon change:")
                .value(String.class, GuildUpdateIconEvent::getOldIconUrl, -1)
                .value(String.class, GuildUpdateIconEvent::getNewIconUrl, 0)
                .value(String.class, GuildUpdateIconEvent::getNewIconUrl, 1)
                .value(Guild.class, GuildUpdateIconEvent::getGuild, 0)
                .author(GuildUpdateIconEvent::getGuild)
                .register();

        // Guild Invite Create Event
        EventRegistryFactory.builder(GuildInviteCreateEvent.class)
                .name("Invite Create Event")
                .patterns("[discord] guild invite create")
                .description("Fired when a invite is created in a guild can be used to get the invite property, the author and the guild.")
                .example("on guild invite create:")
                .value(Channel.class, GuildInviteCreateEvent::getChannel, 0)
                .value(Invite.class, GuildInviteCreateEvent::getInvite, 0)
                .value(Guild.class, GuildInviteCreateEvent::getGuild, 0)
                .author(GuildInviteCreateEvent::getGuild)
                .register();

        // Guild Invite Delete Event
        EventRegistryFactory.builder(GuildInviteDeleteEvent.class)
                .name("Invite Delete Event")
                .patterns("[discord] guild invite delete")
                .description("Fired when a invite is deleted from a guild can be used to get the invite property, the author and the guild.")
                .example("on guild invite delete:")
                .value(Channel.class, GuildInviteDeleteEvent::getChannel, 0)
                .value(Guild.class, GuildInviteDeleteEvent::getGuild, 0)
                .author(GuildInviteDeleteEvent::getGuild)
                .register();

        // Guild Join Event
        EventRegistryFactory.builder(GuildJoinEvent.class)
                .name("Guild Join Event")
                .patterns("[discord] bot join guild")
                .description("Fired when the bot joins in a guild.")
                .example("on bot join guild:")
                .value(Guild.class, GuildJoinEvent::getGuild, 0)
                .register();

        // Guild Log Entry Event
        EventRegistryFactory.builder(GuildAuditLogEntryCreateEvent.class)
                .name("Guild Log Entry Create Event")
                .patterns("[discord] guild log [entry] create")
                .description("Fired when a new log entry is created in a guild.")
                .example("on guild log entry create:")
                .value(AuditLogEntry.class, GuildAuditLogEntryCreateEvent::getEntry, 0)
                .value(Guild.class, GuildAuditLogEntryCreateEvent::getGuild, 0)
                .author(GuildAuditLogEntryCreateEvent::getGuild)
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getEntry().getUserIdLong()))
                .register();

        // Guild Name Event
        EventRegistryFactory.builder(GuildUpdateNameEvent.class)
                .name("Guild Name Event")
                .patterns("[discord] guild name (update|change)")
                .description("Fired when the name of a guild is changed can be used to get the old/new name.")
                .example("on guild name change:")
                .value(String.class, GuildUpdateNameEvent::getOldValue, -1)
                .value(String.class, GuildUpdateNameEvent::getNewValue, 0)
                .value(String.class, GuildUpdateNameEvent::getNewValue, 1)
                .value(Guild.class, GuildUpdateNameEvent::getGuild, 0)
                .author(GuildUpdateNameEvent::getGuild)
                .register();

        // Guild Owner Event
        EventRegistryFactory.builder(GuildUpdateOwnerEvent.class)
                .name("Guild Owner Event")
                .patterns("[discord] guild owner (change|update)")
                .description("Fired when a owner of a guild changes can be used to get the old/new owner, the author and the guild.")
                .example("on guild owner change:")
                .value(Member.class, GuildUpdateOwnerEvent::getOldOwner, -1)
                .value(Member.class, GuildUpdateOwnerEvent::getNewOwner, 0)
                .value(Member.class, GuildUpdateOwnerEvent::getNewOwner, 1)
                .value(Guild.class, GuildUpdateOwnerEvent::getGuild)
                .author(GuildUpdateOwnerEvent::getGuild)
                .register();

        // Guild Splash Event
        EventRegistryFactory.builder(GuildUpdateSplashEvent.class)
                .name("Guild Splash Event")
                .patterns("[discord] guild splash (change|update)")
                .description("Fired when a banner of a guild changes can be used to get the old/new banner, the author and the guild.")
                .example("on guild splash change:")
                .value(String.class, GuildUpdateSplashEvent::getOldSplashUrl, -1)
                .value(String.class, GuildUpdateSplashEvent::getNewSplashUrl, 0)
                .value(String.class, GuildUpdateSplashEvent::getNewSplashUrl, 1)
                .value(Guild.class, GuildUpdateSplashEvent::getGuild, 0)
                .author(GuildUpdateSplashEvent::getGuild)
                .register();

        // Guild Unban Event
        EventRegistryFactory.builder(GuildUnbanEvent.class)
                .name("Guild Unban Event")
                .patterns("[discord] guild [user] unban")
                .description("Fired when a user is unbanned can be used to get the unbanned user, the author and the guild.")
                .example("on guild unban:")
                .value(User.class, GuildUnbanEvent::getUser, 0)
                .value(Guild.class, GuildUnbanEvent::getGuild, 0)
                .author(GuildUnbanEvent::getGuild)
                .register();

        // Guild Voice Deafen Event
        EventRegistryFactory.builder(GuildVoiceGuildDeafenEvent.class)
                .name("Guild Voice Deafen Event")
                .patterns("[discord] guild [voice] deafen[ed]")
                .description("Fired when a member is deafened or undeafened by the guild.")
                .example("on guild voice deafen:")
                .value(Boolean.class, GuildVoiceGuildDeafenEvent::isGuildDeafened, 0)
                .value(Member.class, GuildVoiceGuildDeafenEvent::getMember, 0)
                .value(Guild.class, GuildVoiceGuildDeafenEvent::getGuild, 0)
                .author(GuildVoiceGuildDeafenEvent::getGuild)
                .register();

        // Guild Voice Mute Event
        EventRegistryFactory.builder(GuildVoiceGuildMuteEvent.class)
                .name("Guild Voice Mute Event")
                .patterns("[discord] guild [voice] mute[d]")
                .description("Fired when a member is muted or unmuted by the guild.")
                .example("on guild voice mute:")
                .value(Boolean.class, GuildVoiceGuildMuteEvent::isGuildMuted, 0)
                .value(Member.class, GuildVoiceGuildMuteEvent::getMember, 0)
                .value(Guild.class, GuildVoiceGuildMuteEvent::getGuild, 0)
                .author(GuildVoiceGuildMuteEvent::getGuild)
                .register();
    }
}