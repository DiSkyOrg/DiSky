package net.itsthesky.disky.elements.events.rework;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

public class BotEvents {

    public static final BuiltEvent<ReadyEvent> READY_EVENT;
    public static final BuiltEvent<GuildReadyEvent> GUILD_READY_EVENT;
    public static final BuiltEvent<ShutdownEvent> SHUTDOWN_EVENT;

    static {
        // Bot Join Event
        // Fired when the bot joins a new guild/server
        EventRegistryFactory.builder(GuildJoinEvent.class)
                .name("Bot Join Event")
                .patterns("bot [guild] join[ed]")
                .description("Fired when the bot joins a new guild/server.",
                        "This event is useful for setting up initial configurations when the bot enters a new server,", 
                        "such as adding default roles, sending welcome messages, or initializing server-specific settings.")
                .example("on bot join:\n\tbroadcast \"Bot joined a new server: %event-guild%!\"")
                .value(Guild.class, GuildJoinEvent::getGuild, 0)
                .register();

        // Bot Leave Event
        // Fired when the bot leaves a guild/server
        EventRegistryFactory.builder(GuildLeaveEvent.class)
                .name("Bot Leave Event")
                .patterns("bot [guild] (leave|left)")
                .description("Fired when the bot leaves a guild/server.",
                        "This event can be used for cleanup operations or logging when the bot is removed from a server,",
                        "either by being kicked, the server being deleted, or the bot owner removing it manually.")
                .example("on bot leave:\n\tbroadcast \"Bot left the server: %event-guild%\"")
                .value(Guild.class, GuildLeaveEvent::getGuild, 0)
                .register();

        // Bot Shutdown Event
        // Fired when the bot is shutting down
        SHUTDOWN_EVENT = EventRegistryFactory.builder(ShutdownEvent.class)
                .name("Bot Shutdown Event")
                .patterns("bot (shutdown|stop)")
                .description("Fired when a bot is shutting down or being stopped.",
                        "This event is triggered when the bot's connection to Discord is closing,",
                        "which can happen during server restarts, plugin reloads, or manual bot shutdowns.",
                        "It provides an opportunity to perform cleanup operations or save data before the bot goes offline.")
                .example("on bot shutdown:\n\tbroadcast \"Bot %event-bot% is shutting down!\"")
                .register();

        // Guild Ready Event
        // Fired when a guild is fully loaded and accessible to the bot
        GUILD_READY_EVENT = EventRegistryFactory.builder(GuildReadyEvent.class)
                .name("Guild Ready Event")
                .patterns("guild (ready|load[ed])")
                .description("Fired when a guild is fully loaded and all its data is accessible.",
                        "This event occurs for each guild the bot is connected to when starting up.",
                        "It's fired before the global Ready event and indicates that guild-specific data", 
                        "like members, channels, and roles have been loaded and are available for use.")
                .example("on guild ready:\n\tbroadcast \"Guild %event-guild% is now fully loaded!\"")
                .value(Guild.class, GuildReadyEvent::getGuild, 0)
                .register();

        // Bot Ready Event
        // Fired when the bot is fully initialized and ready
        READY_EVENT = EventRegistryFactory.builder(ReadyEvent.class)
                .name("Bot Ready Event")
                .patterns("(ready|bot load[ed])")
                .description("Fired when a bot is fully loaded and connected to Discord.",
                        "This event is triggered once all guilds are ready and the bot's connection to Discord",
                        "is completely established. This is the ideal event to use for initialization code that",
                        "needs to run once when the bot starts up, such as scheduling tasks or initializing resources.")
                .example("on bot loaded:\n\tbroadcast \"Bot %event-bot% is now online and ready!\"")
                .register();
    }
}