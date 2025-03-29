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
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.itsthesky.disky.api.events.rework.CopyEventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@CopyEventCategory(GuildEvents.class)
public class GuildInviteEvents {

    static {
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

        EventRegistryFactory.builder(GuildInviteDeleteEvent.class)
                .name("Invite Delete Event")
                .patterns("[discord] guild invite delete")
                .description("Fired when an invite is deleted from a guild. Can be used to get the invite code, the channel, the author and the guild.")
                .example("on guild invite delete:\n\tbroadcast \"Invite deleted from %event-channel% in %event-guild%\"")
                .value(Channel.class, GuildInviteDeleteEvent::getChannel, 0)
                .value(Guild.class, GuildInviteDeleteEvent::getGuild, 0)
                .author(GuildInviteDeleteEvent::getGuild)
                .register();
    }

}
