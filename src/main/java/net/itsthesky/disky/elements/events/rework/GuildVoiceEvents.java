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

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.itsthesky.disky.api.events.rework.CopyEventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;

@CopyEventCategory(GuildEvents.class)
public class GuildVoiceEvents {

    static {
        EventRegistryFactory.builder(GuildVoiceGuildDeafenEvent.class)
                .name("Guild Voice Deafen Event")
                .patterns("[discord] guild [voice] deafen[ed]")
                .description("Fired when a member is deafened or undeafened by the guild. Can be used to track moderation actions in voice channels.")
                .example("on guild voice deafen:\n    if event-boolean is true:\n        broadcast \"%event-member% was deafened in %event-guild%\"\n    else:\n        broadcast \"%event-member% was undeafened in %event-guild%\"")
                .value(Boolean.class, GuildVoiceGuildDeafenEvent::isGuildDeafened, 0)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

        EventRegistryFactory.builder(GuildVoiceGuildMuteEvent.class)
                .name("Guild Voice Mute Event")
                .patterns("[discord] guild [voice] mute[d]")
                .description("Fired when a member is muted or unmuted by the guild. Can be used to track moderation actions in voice channels.")
                .example("on guild voice mute:\n    if event-boolean is true:\n        broadcast \"%event-member% was muted in %event-guild%\"\n    else:\n        broadcast \"%event-member% was unmuted in %event-guild%\"")
                .value(Boolean.class, GuildVoiceGuildMuteEvent::isGuildMuted, 0)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

        EventRegistryFactory.builder(GuildVoiceMuteEvent.class)
                .name("Guild Voice Mute Event")
                .patterns("[discord] guild [voice] mute[d]")
                .description("Fired when a member is muted or unmuted by the guild. Can be used to track moderation actions in voice channels.")
                .example("on guild voice mute:\n    if event-boolean is true:\n        broadcast \"%event-member% was muted in %event-guild%\"\n    else:\n        broadcast \"%event-member% was unmuted in %event-guild%\"")
                .value(Boolean.class, GuildVoiceMuteEvent::isMuted, 0)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

        EventRegistryFactory.builder(GuildVoiceRequestToSpeakEvent.class)
                .name("Guild Voice Request To Speak Event")
                .patterns("[discord] guild [voice] request to speak")
                .description("Fired when a member requests to speak in a stage channel. Can be used to track moderation actions in voice channels.",
                        "You may use `event-boolean` to check if the state is true (user requested to speak) or false (user cancelled the request).")
                .example("on guild voice request to speak:\n    if event-boolean is true:\n        broadcast \"%event-member% requested to speak in %event-guild%\"\n    else:\n        broadcast \"%event-member% cancelled their request to speak in %event-guild%\"")
                .value(Boolean.class, evt -> evt.getNewTime() != null)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .customTimedExpressions("request to speak", Date.class,
                        evt -> SkriptUtils.convertDateTime(evt.getNewTime()),
                        evt -> SkriptUtils.convertDateTime(evt.getOldTime()))
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

        EventRegistryFactory.builder(GuildVoiceStreamEvent.class)
                .name("Guild Voice Stream Event")
                .patterns("[discord] guild [voice] stream[ing]")
                .description("Fired when a member starts or stops streaming in a voice channel. Can be used to track moderation actions in voice channels.",
                        "You may use `event-boolean` to check if the state is true (user started streaming) or false (user stopped streaming).",
                        "",
                        "!!! warning \"This **DOES NOT** include camera! Use the `GuildVoice Video Event` for that.\"")
                .example("on guild voice stream:\n    if event-boolean is true:\n        broadcast \"%event-member% started streaming in %event-guild%\"\n    else:\n        broadcast \"%event-member% stopped streaming in %event-guild%\"")
                .value(Boolean.class, GuildVoiceStreamEvent::isStream, 0)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

        EventRegistryFactory.builder(GuildVoiceVideoEvent.class)
                .name("Guild Voice Video Event")
                .patterns("[discord] guild [voice] video[ing]")
                .description("Fired when a member starts or stops its camera in a voice channel. Can be used to track moderation actions in voice channels.",
                        "You may use `event-boolean` to check if the state is true (user started its camera) or false (user stopped its camera).",
                        "",
                        "!!! warning \"This **DOES NOT** include streams! Use the `Guild Voice Stream Event` instead.\"")
                .example("on guild voice video:\n    if event-boolean is true:\n        broadcast \"%event-member% started video in %event-guild%\"\n    else:\n        broadcast \"%event-member% stopped video in %event-guild%\"")
                .value(Boolean.class, GuildVoiceVideoEvent::isSendingVideo, 0)
                .value(Member.class, GenericGuildVoiceEvent::getMember, 0)
                .value(Guild.class, GenericGuildVoiceEvent::getGuild, 0)
                .author(GenericGuildVoiceEvent::getGuild)
                .register();

    }

}
