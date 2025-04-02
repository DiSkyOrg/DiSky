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
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

public class PollEvents {

    static {
        EventRegistryFactory.builder(MessagePollVoteAddEvent.class)
                .eventCategory(MessageEvents.class)
                .name("Poll Vote Add")
                .patterns("[message] poll vote add[ed]")
                .description("Fired when a user adds their vote to a poll in a message.")
                .example("on poll vote add:")

                .restValue("message", MessagePollVoteAddEvent::retrieveMessage)
                .restValue("member", MessagePollVoteAddEvent::retrieveMember)
                .restValue("user", MessagePollVoteAddEvent::retrieveUser)

                .channelValues(GenericMessageEvent::getChannel)
                .value(Guild.class, event -> event.isFromGuild() ? event.getGuild() : null)
                .value(Number.class, MessagePollVoteAddEvent::getMessageIdLong)

                .author(event -> event.isFromGuild() ? event.getGuild() : null)

                .register();

        EventRegistryFactory.builder(MessagePollVoteRemoveEvent.class)
                .eventCategory(MessageEvents.class)
                .name("Poll Vote Remove")
                .patterns("[message] poll vote remove[d]")
                .description("Fired when a user removes their vote from a poll in a message.")
                .example("on poll vote remove:")
                .example("    send \"User %event-user% removed their vote from a poll in %event-channel%!\" to console")

                .restValue("message", MessagePollVoteRemoveEvent::retrieveMessage)
                .restValue("member", MessagePollVoteRemoveEvent::retrieveMember)
                .restValue("user", MessagePollVoteRemoveEvent::retrieveUser)

                .channelValues(GenericMessageEvent::getChannel)
                .value(Number.class, MessagePollVoteRemoveEvent::getMessageIdLong)

                .author(event -> event.isFromGuild() ? event.getGuild() : null)

                .register();
    }

}
