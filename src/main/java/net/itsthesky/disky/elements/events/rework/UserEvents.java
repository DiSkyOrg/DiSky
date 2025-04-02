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

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.*;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@EventCategory(name = "User/Member Events", description = {
        "Events related to user/member actions and updates.",
        "Keep in mind most user update events requires a member to be seen by the bot in any guild, with the intent 'guild presence' enabled."
})
public class UserEvents {

    static {
        // User Activity Order Update Event
        // Fired when a user's activity order changes
        EventRegistryFactory.builder(UserUpdateActivityOrderEvent.class)
                .name("User Activity Order Update Event")
                .patterns("[discord] user activity [order] (change|update)")
                .description("Fired when a user changes their activity order.",
                        "This event is triggered when a user starts a new activity or changes between activities.",
                        "Activities include playing games, listening to music, streaming, or custom status messages.")
                .example("on user activity change:\n    broadcast \"%event-user% is now %first element of user activities%\"")
                .listExpression("user activit(y|ies)", Activity.class,
                        evt -> evt.getNewValue().toArray(Activity[]::new))
                .value(User.class, UserUpdateActivityOrderEvent::getUser)
                .value(Guild.class, UserUpdateActivityOrderEvent::getGuild)
                .value(Member.class, UserUpdateActivityOrderEvent::getMember)
                .register();

        // User Avatar Update Event
        // Fired when a user changes their avatar
        EventRegistryFactory.builder(UserUpdateAvatarEvent.class)
                .name("User Avatar Update Event")
                .patterns("[discord] user avatar (change|update)")
                .description("Fired when a user changes their avatar.",
                        "This event provides access to both the old and new avatar URLs.",
                        "It can be used for monitoring profile changes or updating cached user information.")
                .example("on user avatar change:\n    broadcast \"%event-user% changed their avatar from %previous avatar url% to %current avatar url%\"")
                .customTimedExpressions("avatar [url]", String.class,
                        UserUpdateAvatarEvent::getNewAvatarUrl,
                        UserUpdateAvatarEvent::getOldAvatarUrl)
                .value(User.class, UserUpdateAvatarEvent::getUser)
                .register();

        // User Discriminator Update Event
        // Fired when a user's discriminator changes
        EventRegistryFactory.builder(UserUpdateDiscriminatorEvent.class)
                .name("User Discriminator Update Event")
                .patterns("[discord] user discriminator (change|update)")
                .description("Fired when a user changes their discriminator.",
                        "The discriminator is the four-digit number following a username (e.g., #1234).",
                        "This event provides access to both the old and new discriminator values.",
                        "Note: With Discord's migration to the new username system, this event may become less relevant.")
                .example("on user discriminator change:\n    broadcast \"%event-user% changed their discriminator from %previous discriminator% to %current discriminator%\"")
                .customTimedExpressions("discriminator", String.class,
                        UserUpdateDiscriminatorEvent::getNewValue,
                        UserUpdateDiscriminatorEvent::getOldValue)
                .value(User.class, UserUpdateDiscriminatorEvent::getUser)
                .register();

        // User Name Update Event
        // Fired when a user changes their username
        EventRegistryFactory.builder(UserUpdateNameEvent.class)
                .name("User Name Update Event")
                .patterns("[discord] user name (change|update)")
                .description("Fired when a user changes their username (not nickname).",
                        "This event provides access to both the old and new usernames.",
                        "It can be used for monitoring identity changes or updating user databases.")
                .example("on user name change:\n    broadcast \"User changed their name from %previous name% to %current name%\"")
                .customTimedExpressions("name", String.class,
                        UserUpdateNameEvent::getNewValue,
                        UserUpdateNameEvent::getOldValue)
                .value(User.class, UserUpdateNameEvent::getUser)
                .register();

        // User Online Status Update Event
        // Fired when a user's online status changes
        EventRegistryFactory.builder(UserUpdateOnlineStatusEvent.class)
                .name("User Online Status Update Event")
                .patterns("[discord] user online status (change|update)")
                .description("Fired when a user changes their online status.",
                        "This event provides access to both the old and new online status values.",
                        "It can be used for tracking user presence, activity patterns, or triggering actions when users come online.")
                .example("on user online status change:\n    if current online status = online:\n        broadcast \"%event-user% has come online\"")
                .customTimedExpressions("online status", OnlineStatus.class,
                        UserUpdateOnlineStatusEvent::getNewValue,
                        UserUpdateOnlineStatusEvent::getOldValue)
                .value(User.class, UserUpdateOnlineStatusEvent::getUser)
                .value(Member.class, UserUpdateOnlineStatusEvent::getMember)
                .value(Guild.class, UserUpdateOnlineStatusEvent::getGuild)
                .register();

        // User Typing Event
        // Fired when a user starts typing in a channel
        EventRegistryFactory.builder(UserTypingEvent.class)
                .name("User Typing Event")
                .patterns("[discord] user typ[e|ing]")
                .description("Fired when a user starts typing in a channel.",
                        "This event is triggered when the typing indicator appears for a user.",
                        "It can be used to detect activity in channels or for interactive bot responses.")
                .example("on user typing:\n    if event-channel is text channel with id \"123456789\":\n        broadcast \"%event-user% is typing in the support channel!\"")
                .value(User.class, UserTypingEvent::getUser)
                .value(Guild.class, UserTypingEvent::getGuild)
                .value(Member.class, UserTypingEvent::getMember)
                .channelValues(UserTypingEvent::getChannel)
                .register();

        // User Global Name Update Event
        // Fired when a user changes their global display name
        EventRegistryFactory.builder(UserUpdateGlobalNameEvent.class)
                .name("User Global Name Update Event")
                .patterns("[discord] user global name (change|update)")
                .description("Fired when a user changes their global display name.",
                        "This event provides access to both the old and new global names.",
                        "With Discord's new username system, this tracks the display name shown across all servers.")
                .example("on user global name change:\n    broadcast \"%event-user% changed their display name from '%previous global name%' to '%current global name%'\"")
                .customTimedExpressions("global name", String.class,
                        UserUpdateGlobalNameEvent::getNewValue,
                        UserUpdateGlobalNameEvent::getOldValue)
                .value(User.class, UserUpdateGlobalNameEvent::getUser)
                .register();
    }
}