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
import net.dv8tion.jda.api.entities.sticker.GuildSticker;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.*;
import net.itsthesky.disky.api.events.rework.CopyEventCategory;
import net.itsthesky.disky.api.events.rework.EventBuilder;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

import java.util.Objects;
import java.util.function.Function;

@EventCategory(
        name = "Guild Sticker Events",
        description = {
                "Events related to guild stickers.",
                "These events are triggered when certain actions occur with stickers, such as creation, deletion, or updates.",
                "",
                "!!! warning \"For those events to work, the [`sticker` cache flag](https://disky.me/docs/bot/policy/#available-cache-flags) and `guild expressions` intent.\""
        }
)
public class GuildStickerEvents {

    static {
        // Guild Sticker Add Event
        // Fired when a sticker is ADDED to a guild
        EventRegistryFactory.builder(GuildStickerAddedEvent.class)
                .name("Guild Sticker Add")
                .patterns("[discord] guild sticker add[ed]")
                .description("Fired when someone or something adds a sticker to a guild.")
                .example("on guild sticker add:\n    send \"New sticker added in %event-guild%: %event-sticker%\" to console")
                .value(Guild.class, GuildStickerAddedEvent::getGuild, 0)
                .value(GuildSticker.class, GuildStickerAddedEvent::getSticker)
                .singleExpression("sticker id", String.class, event -> event.getSticker().getId())
                .restValue("author", event -> event.getGuild().retrieveAuditLogs()
                        .map(array -> event.getGuild().getMember(Objects.requireNonNull(array.get(0).getUser()))))
                .register();

        // Guild Sticker Removed Event
        // Fired when a sticker is REMOVED from a guild
        EventRegistryFactory.builder(GuildStickerRemovedEvent.class)
                .name("Guild Sticker Remove")
                .patterns("[discord] guild sticker remove[d]")
                .description("Fired when someone or something removes a sticker from a guild")
                .example("on guild sticker remove:\n    broadcast \"%event-user% removed %event-sticker% from %event-guild%\"")
                .value(Guild.class, GuildStickerRemovedEvent::getGuild, 0)
                .value(GuildSticker.class, GuildStickerRemovedEvent::getSticker)
                .singleExpression("sticker id", String.class, event -> event.getSticker().getId())
                .restValue("sticker", event -> event.getGuild().retrieveSticker(event.getSticker()))
                .restValue("author", event -> event.getGuild().retrieveAuditLogs()
                        .map(array -> event.getGuild().getMember(Objects.requireNonNull(array.get(0).getUser()))))
                .register();

        stickerUpdateBuilder(GuildStickerUpdateDescriptionEvent.class, String.class, "Description").register();
        stickerUpdateBuilder(GuildStickerUpdateNameEvent.class, String.class, "Name").register();
        stickerUpdateBuilder(GuildStickerUpdateAvailableEvent.class, Boolean.class, "Available State").register();

        EventRegistryFactory.builder(GuildStickerUpdateTagsEvent.class)
                .name("Guild Sticker Tags Update")
                .patterns("[discord] guild sticker tags update[d]")
                .description("Fired when the tags of a sticker is updated.")
                .example("on guild sticker tags update:\n    broadcast \"Tags of %event-sticker% changed! %old tags% -> %new tags%\"")
                .customTimedListExpressions("tags", String.class,
                        e -> e.getNewValue().toArray(new String[0]),
                        e -> e.getOldValue().toArray(new String[0]))
                .value(Guild.class, GenericGuildStickerUpdateEvent::getGuild, 0)
                .value(GuildSticker.class, GenericGuildStickerUpdateEvent::getSticker)
                .register();
    }

    private static <T, E extends GenericGuildStickerUpdateEvent<T>> EventBuilder<E> stickerUpdateBuilder(
            Class<E> eventClass,
            Class<T> propertyClass,
            String propertyName
    ) {
        final var lower = propertyName.toLowerCase();
        return EventRegistryFactory.builder(eventClass)
                .name("Guild Sticker "+ propertyName +" Update")
                .patterns("[discord] guild sticker "+ lower +" update[d]")
                .description("Fired when the "+ propertyName +" of a sticker is updated.")
                .example("on guild sticker "+ propertyName +" update:\n    broadcast \""+ propertyName + " of %event-sticker% changed! %old "+ lower +"% -> %new "+ lower +"%\"")
                .customTimedExpressions(lower, propertyClass, GenericGuildStickerUpdateEvent::getNewValue, GenericGuildStickerUpdateEvent::getOldValue)
                .value(Guild.class, GenericGuildStickerUpdateEvent::getGuild, 0)
                .value(GuildSticker.class, GenericGuildStickerUpdateEvent::getSticker);
    }

}
