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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.guild.update.*;
import net.itsthesky.disky.api.events.rework.CopyEventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

@CopyEventCategory(GuildEvents.class)
public class GuildUpdateEvents {

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
    }


}
