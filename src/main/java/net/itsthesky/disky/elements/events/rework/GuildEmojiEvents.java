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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.GenericEmojiEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.events.emoji.update.GenericEmojiUpdateEvent;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@EventCategory(
        name = "Guild Emoji Events",
        description = {
                "Events related to guild emojis.",
                "These events are triggered when certain actions occur with emojis, such as creation, deletion, or updates.",
                "",
                "!!! warning \"For those events to work, the [`emoji` cache flag](https://disky.me/docs/bot/policy/#available-cache-flags) and `guild expressions` intent.\""
        }
)
public class GuildEmojiEvents {

    static {
        EventRegistryFactory.builder(EmojiAddedEvent.class)
                .name("Emoji Add")
                .patterns("[discord] emoji add[ed]")
                .description("Fired when a new emoji is added to a guild.")
                .example("on guild sticker add:\n    send \"New sticker added in %event-guild%: %event-sticker%\" to console")
                .value(Guild.class, GenericEmojiEvent::getGuild, 0)
                .value(Emote.class, e -> new Emote(e.getEmoji()))
                .singleExpression("managed [state]", Boolean.class, GenericEmojiEvent::isManaged)
                .register();

        EventRegistryFactory.builder(EmojiRemovedEvent.class)
                .name("Emoji Remove")
                .patterns("[discord] emoji remove[d]")
                .description("Fired when an emoji is removed from a guild.")
                .example("on guild sticker remove:\n    send \"Sticker %event-sticker% removed from %event-guild%\" to console")
                .value(Guild.class, GenericEmojiEvent::getGuild, 0)
                .value(Emote.class, e -> new Emote(e.getEmoji()))
                .singleExpression("managed [state]", Boolean.class, GenericEmojiEvent::isManaged)
                .register();

        EventRegistryFactory.builder(EmojiUpdateRolesEvent.class)
                .name("Emoji Roles Update")
                .patterns("[discord] emoji roles update[d]")
                .description("Fired when the roles of an emoji are updated.")
                .example("on guild sticker roles update:\n    send \"Sticker %event-sticker% roles updated in %event-guild%\" to console")
                .customTimedListExpressions("roles", Role.class,
                        e -> e.getNewValue().toArray(new Role[0]),
                        e -> e.getOldValue().toArray(new Role[0]))
                .value(Guild.class, GenericEmojiUpdateEvent::getGuild, 0)
                .value(Emote.class, e -> new Emote(e.getEmoji()))
                .register();

        EventRegistryFactory.builder(EmojiUpdateNameEvent.class)
                .name("Emoji Name Update")
                .patterns("[discord] emoji name update[d]")
                .description("Fired when the name of an emoji is updated.")
                .example("on guild sticker name update:\n    send \"Sticker %event-sticker% name updated in %event-guild%\" to console")
                .customTimedExpressions("name", String.class, EmojiUpdateNameEvent::getNewValue, EmojiUpdateNameEvent::getOldValue)
                .value(Guild.class, GenericEmojiUpdateEvent::getGuild, 0)
                .value(Emote.class, e -> new Emote(e.getEmoji()))
                .register();
    }
}
