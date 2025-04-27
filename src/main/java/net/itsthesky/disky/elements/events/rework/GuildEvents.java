package net.itsthesky.disky.elements.events.rework;

import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.sticker.StickerSnowflake;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.sticker.GenericGuildStickerEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GenericGuildStickerUpdateEvent;
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

        // Guild Sticker Add Event
        // Fired when a sticker is ADDED to a guild
        EventRegistryFactory.builder(GuildStickerAddedEvent.class)
                .name("Guild Sticker Add")
                .patterns("[discord] guild sticker add[ed]")
                .description("Fired when someone or something adds a sticker to a guild.")
                .example("on guild sticker add:\n    broadcast \"%event-user% added %event-string% to %event-guild%\"")
                .value(Guild.class, GuildStickerAddedEvent::getGuild, 0)
                .restValue("sticker", event -> event.getGuild().retrieveSticker(event.getSticker()))
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getGuild().getOwnerId()))
                .register();

        // Guild Sticker Removed Event
        // Fired when a sticker is REMOVED from a guild
        EventRegistryFactory.builder(GuildStickerRemovedEvent.class)
                .name("Guild Sticker Remove")
                .patterns("[discord] guild sticker remove[d]")
                .description("Fired when someone or something removes a sticker from a guild")
                .example("on guild sticker remove:\n    broadcast \"%event-user% removed %event-sticker% from %event-guild%\"")
                .value(Guild.class, GuildStickerRemovedEvent::getGuild, 0)
                .restValue("sticker", event -> event.getGuild().retrieveSticker(event.getSticker()))
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getGuild().getOwnerId()))
                .register();

        // Guild Sticker Update Event
        // Fired when a sticker updated in a guild
        EventRegistryFactory.builder(GenericGuildStickerUpdateEvent.class)
                .name("Guild Sticker Update")
                .patterns("[discord] guild sticker update[d]")
                .description("Fired when someone or something updates a sticker in a guild")
                .example("on guild sticker update:\n    broadcast \"%event-user% updated %event-sticker% in %event-guild%\"")
                .value(Guild.class, GenericGuildStickerUpdateEvent::getGuild, 0)
                .restValue("sticker", event -> event.getGuild().retrieveSticker(event.getSticker()))
                .restValue("author", event -> event.getGuild().retrieveMemberById(event.getGuild().getOwnerId()))
                .register();
    }
}