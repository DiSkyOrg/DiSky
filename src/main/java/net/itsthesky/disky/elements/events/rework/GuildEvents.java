package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@EventCategory(name = "Guild Events", description = {
        "Events related to guilds (servers) on Discord.",
        "These events are triggered when certain actions occur within a guild, such as changes to settings, member actions, or administrative tasks."
})
public class GuildEvents {

    static {
        // Guild AutoMod Execution Event
        // Fired when an automod rule is triggered and an automatic response is executed
        EventRegistryFactory.builder(AutoModExecutionEvent.class)
                .name("AutoMod Execution")
                .patterns("[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .example("on automod execute:\n    broadcast \"AutoMod rule triggered by %event-user% in %event-channel%\"")
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
                .example("on guild ban:\n    broadcast \"%event-user% was banned from %event-guild%\"")
                .value(User.class, GuildBanEvent::getUser, 0)
                .value(Guild.class, GuildBanEvent::getGuild, 0)
                .author(GuildBanEvent::getGuild)
                .register();

        // Guild Join Event
        // Fired when the bot joins a new guild
        EventRegistryFactory.builder(GuildJoinEvent.class)
                .name("Guild Join Event")
                .patterns("[discord] bot join guild")
                .description("Fired when the bot joins a guild. Use this to set up initial configurations or welcome messages.")
                .example("on bot join guild:\n    broadcast \"Bot joined %event-guild%!\"")
                .value(Guild.class, GuildJoinEvent::getGuild, 0)
                .register();

        // Guild Log Entry Event
        // Fired when a new audit log entry is created in a guild
        EventRegistryFactory.builder(GuildAuditLogEntryCreateEvent.class)
                .name("Guild Log Entry Create Event")
                .patterns("[discord] guild log [entry] create")
                .description("Fired when a new log entry is created in a guild. Can be used to monitor administrative actions within a guild.")
                .example("on guild log entry create:\n    broadcast \"New audit log entry created in %event-guild% for action type %event-entry's type%\"")
                .value(AuditLogEntry.class, GuildAuditLogEntryCreateEvent::getEntry, 0)
                .value(Guild.class, GuildAuditLogEntryCreateEvent::getGuild, 0)
                .author(GuildAuditLogEntryCreateEvent::getGuild)
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getEntry().getUserIdLong()))
                .register();

        // Guild Unban Event
        // Fired when a user is unbanned from a guild
        EventRegistryFactory.builder(GuildUnbanEvent.class)
                .name("Guild Unban Event")
                .patterns("[discord] guild [user] unban")
                .description("Fired when a user is unbanned from a guild. Can be used to get the unbanned user, the author and the guild.")
                .example("on guild unban:\n    broadcast \"%event-user% was unbanned from %event-guild%\"")
                .value(User.class, GuildUnbanEvent::getUser, 0)
                .value(Guild.class, GuildUnbanEvent::getGuild, 0)
                .author(GuildUnbanEvent::getGuild)
                .register();
    }
}