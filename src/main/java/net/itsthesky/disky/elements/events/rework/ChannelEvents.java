package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Timespan;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;

import java.time.OffsetDateTime;
import java.util.List;

public class ChannelEvents {

    static {
        // Channel Create Event
        EventRegistryFactory.builder(ChannelCreateEvent.class)
                .name("Channel Create Event")
                .patterns("[discord] channel creat(e|ion)")
                .description("Fired when a channel is created.")
                .example("on channel creation:\n\tbroadcast \"%event-channel%, %event-guild%\"")
                .value(Guild.class, ChannelCreateEvent::getGuild)
                .channelValues(ChannelCreateEvent::getChannel)
                .author(ChannelCreateEvent::getGuild)
                .register();

        // Channel Delete Event
        EventRegistryFactory.builder(ChannelDeleteEvent.class)
                .name("Channel Delete Event")
                .patterns("[discord] channel delet(e|ion)")
                .description("Fired when a channel is deleted.")
                .example("on channel deletion:\n\tbroadcast \"%event-channel%, %event-guild%\"")
                .value(Guild.class, ChannelDeleteEvent::getGuild)
                .channelValues(ChannelDeleteEvent::getChannel)
                .author(ChannelDeleteEvent::getGuild)
                .register();

        // Channel Update Name Event
        EventRegistryFactory.builder(ChannelUpdateNameEvent.class)
                .name("Channel Name Update Event")
                .patterns("[discord] channel name (change|update)")
                .description("Fired when a channel's name is changed.")
                .example("on channel name change:\n\tbroadcast \"Channel %event-channel% was renamed from %past event-string% to %event-string%\"")
                .value(String.class, ChannelUpdateNameEvent::getOldValue, -1)
                .value(String.class, ChannelUpdateNameEvent::getNewValue, 0)
                .value(String.class, ChannelUpdateNameEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateNameEvent::getGuild)
                .channelValues(ChannelUpdateNameEvent::getChannel)
                .author(ChannelUpdateNameEvent::getGuild)
                .register();

        // Channel Update Topic Event
        EventRegistryFactory.builder(ChannelUpdateTopicEvent.class)
                .name("Channel Topic Update Event")
                .patterns("[discord] channel topic (change|update)")
                .description("Fired when a channel's topic is changed.")
                .example("on channel topic change:\n\tbroadcast \"Channel %event-channel% had its topic changed from '%past event-string%' to '%event-string%'\"")
                .value(String.class, ChannelUpdateTopicEvent::getOldValue, -1)
                .value(String.class, ChannelUpdateTopicEvent::getNewValue, 0)
                .value(String.class, ChannelUpdateTopicEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateTopicEvent::getGuild)
                .channelValues(ChannelUpdateTopicEvent::getChannel)
                .author(ChannelUpdateTopicEvent::getGuild)
                .register();

        // Channel Update NSFW Event
        EventRegistryFactory.builder(ChannelUpdateNSFWEvent.class)
                .name("Channel NSFW Update Event")
                .patterns("[discord] channel nsfw (change|update)")
                .description("Fired when a channel's NSFW status is changed.")
                .example("on channel nsfw change:\n\tbroadcast \"Channel %event-channel% NSFW status changed from %past event-boolean% to %event-boolean%\"")
                .value(Boolean.class, ChannelUpdateNSFWEvent::getOldValue, -1)
                .value(Boolean.class, ChannelUpdateNSFWEvent::getNewValue, 0)
                .value(Boolean.class, ChannelUpdateNSFWEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateNSFWEvent::getGuild)
                .channelValues(ChannelUpdateNSFWEvent::getChannel)
                .author(ChannelUpdateNSFWEvent::getGuild)
                .register();

        // Channel Update Position Event
        EventRegistryFactory.builder(ChannelUpdatePositionEvent.class)
                .name("Channel Position Update Event")
                .patterns("[discord] channel position (change|update)")
                .description("Fired when a channel's position is changed.")
                .example("on channel position change:\n\tbroadcast \"Channel %event-channel% position changed from %past event-number% to %event-number%\"")
                .value(Integer.class, ChannelUpdatePositionEvent::getOldValue, -1)
                .value(Integer.class, ChannelUpdatePositionEvent::getNewValue, 0)
                .value(Integer.class, ChannelUpdatePositionEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdatePositionEvent::getGuild)
                .channelValues(ChannelUpdatePositionEvent::getChannel)
                .author(ChannelUpdatePositionEvent::getGuild)
                .register();

        // Channel Update Parent Event
        EventRegistryFactory.builder(ChannelUpdateParentEvent.class)
                .name("Channel Parent Update Event")
                .patterns("[discord] channel parent (change|update)")
                .description("Fired when a channel's parent category is changed.")
                .example("on channel parent change:\n\tbroadcast \"Channel %event-channel% was moved from %past event-category% to %event-category%\"")
                .value(Channel.class, event -> event.getOldValue(), -1)
                .value(Channel.class, event -> event.getNewValue(), 0)
                .value(Channel.class, event -> event.getNewValue(), 1)
                .value(Guild.class, ChannelUpdateParentEvent::getGuild)
                .channelValues(ChannelUpdateParentEvent::getChannel)
                .author(ChannelUpdateParentEvent::getGuild)
                .register();

        // Channel Update Slowmode Event
        EventRegistryFactory.builder(ChannelUpdateSlowmodeEvent.class)
                .name("Channel Slowmode Update Event")
                .patterns("[discord] channel slowmode (change|update)")
                .description("Fired when a channel's slowmode setting is changed.")
                .example("on channel slowmode change:\n\tbroadcast \"Channel %event-channel% slowmode changed from %past event-number% to %event-number% seconds\"")
                .value(Integer.class, ChannelUpdateSlowmodeEvent::getOldValue, -1)
                .value(Integer.class, ChannelUpdateSlowmodeEvent::getNewValue, 0)
                .value(Integer.class, ChannelUpdateSlowmodeEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateSlowmodeEvent::getGuild)
                .channelValues(ChannelUpdateSlowmodeEvent::getChannel)
                .author(ChannelUpdateSlowmodeEvent::getGuild)
                .register();

        // Channel Update Type Event
        EventRegistryFactory.builder(ChannelUpdateTypeEvent.class)
                .name("Channel Type Update Event")
                .patterns("[discord] channel type (change|update)")
                .description("Fired when a channel's type is changed.")
                .example("on channel type change:\n\tbroadcast \"Channel %event-channel% type changed from %past event-channeltype% to %event-channeltype%\"")
                .value(ChannelType.class, ChannelUpdateTypeEvent::getOldValue, -1)
                .value(ChannelType.class, ChannelUpdateTypeEvent::getNewValue, 0)
                .value(ChannelType.class, ChannelUpdateTypeEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateTypeEvent::getGuild)
                .channelValues(ChannelUpdateTypeEvent::getChannel)
                .author(ChannelUpdateTypeEvent::getGuild)
                .register();

        // Channel Update User Limit Event
        EventRegistryFactory.builder(ChannelUpdateUserLimitEvent.class)
                .name("Channel User Limit Update Event")
                .patterns("[discord] channel user limit (change|update)")
                .description("Fired when a voice channel's user limit is changed.")
                .example("on channel user limit change:\n\tbroadcast \"Channel %event-channel% user limit changed from %past event-number% to %event-number%\"")
                .value(Integer.class, ChannelUpdateUserLimitEvent::getOldValue, -1)
                .value(Integer.class, ChannelUpdateUserLimitEvent::getNewValue, 0)
                .value(Integer.class, ChannelUpdateUserLimitEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateUserLimitEvent::getGuild)
                .channelValues(ChannelUpdateUserLimitEvent::getChannel)
                .author(ChannelUpdateUserLimitEvent::getGuild)
                .register();

        // Channel Update Bitrate Event
        EventRegistryFactory.builder(ChannelUpdateBitrateEvent.class)
                .name("Channel Bitrate Update Event")
                .patterns("[discord] channel bitrate (change|update)")
                .description("Fired when a voice channel's bitrate is changed.")
                .example("on channel bitrate change:\n\tbroadcast \"Channel %event-channel% bitrate changed from %past event-number% to %event-number%\"")
                .value(Integer.class, ChannelUpdateBitrateEvent::getOldValue, -1)
                .value(Integer.class, ChannelUpdateBitrateEvent::getNewValue, 0)
                .value(Integer.class, ChannelUpdateBitrateEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateBitrateEvent::getGuild)
                .channelValues(ChannelUpdateBitrateEvent::getChannel)
                .author(ChannelUpdateBitrateEvent::getGuild)
                .register();

        // Channel Update Region Event
        EventRegistryFactory.builder(ChannelUpdateRegionEvent.class)
                .name("Channel Region Update Event")
                .patterns("[discord] channel region (change|update)")
                .description("Fired when a voice channel's region is changed.")
                .example("on channel region change:\n\tbroadcast \"Channel %event-channel% region changed from %past event-string% to %event-string%\"")

                .value(String.class, evt -> evt.getOldValue() == null ? null : evt.getOldValue().name().toLowerCase())
                .value(String.class, evt -> evt.getNewValue() == null ? null : evt.getNewValue().name().toLowerCase(), 0)
                .value(String.class, evt -> evt.getNewValue() == null ? null : evt.getNewValue().name().toLowerCase(), 1)

                .value(Guild.class, ChannelUpdateRegionEvent::getGuild)
                .channelValues(ChannelUpdateRegionEvent::getChannel)
                .author(ChannelUpdateRegionEvent::getGuild)
                .register();

        // Channel Update Voice Status Event
        EventRegistryFactory.builder(ChannelUpdateVoiceStatusEvent.class)
                .name("Channel Voice Status Update Event")
                .patterns("[discord] channel voice status (change|update)")
                .description("Fired when a voice channel's status (video or voice) is changed.")
                .example("on channel voice status change:")
                .value(String.class, GenericChannelUpdateEvent::getOldValue, -1)
                .value(String.class, GenericChannelUpdateEvent::getNewValue, 0)
                .value(String.class, GenericChannelUpdateEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateVoiceStatusEvent::getGuild)
                .channelValues(ChannelUpdateVoiceStatusEvent::getChannel)
                .author(ChannelUpdateVoiceStatusEvent::getGuild)
                .register();

        // Channel Update Applied Tags Event
        EventRegistryFactory.builder(ChannelUpdateAppliedTagsEvent.class)
                .name("Channel Applied Tags Update Event")
                .patterns("[discord] channel [applied] tags (change|update)")
                .description("Fired when a forum channel's applied tags are changed.")
                .example("on channel tags change:")
                .value(List.class, ChannelUpdateAppliedTagsEvent::getOldValue, -1)
                .value(List.class, ChannelUpdateAppliedTagsEvent::getNewValue, 0)
                .value(List.class, ChannelUpdateAppliedTagsEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateAppliedTagsEvent::getGuild)
                .channelValues(ChannelUpdateAppliedTagsEvent::getChannel)
                .author(ChannelUpdateAppliedTagsEvent::getGuild)
                .register();

        // Channel Update Archived Event
        EventRegistryFactory.builder(ChannelUpdateArchivedEvent.class)
                .name("Channel Archived Update Event")
                .patterns("[discord] channel archived (change|update)")
                .description("Fired when a thread channel's archived status is changed.")
                .example("on channel archived change:")
                .customTimedExpressions("[channel] archive[d] state", Boolean.class,
                        GenericChannelUpdateEvent::getNewValue, GenericChannelUpdateEvent::getOldValue)
                .value(Guild.class, ChannelUpdateArchivedEvent::getGuild)
                .channelValues(ChannelUpdateArchivedEvent::getChannel)
                .author(ChannelUpdateArchivedEvent::getGuild)
                .register();

        // Channel Update Archive Timestamp Event
        EventRegistryFactory.builder(ChannelUpdateArchiveTimestampEvent.class)
                .name("Channel Archive Timestamp Update Event")
                .patterns("[discord] channel archive timestamp (change|update)")
                .description("Fired when a thread channel's archive timestamp is changed.")
                .example("on channel archive timestamp change:")
                .value(OffsetDateTime.class, ChannelUpdateArchiveTimestampEvent::getOldValue, -1)
                .value(OffsetDateTime.class, ChannelUpdateArchiveTimestampEvent::getNewValue, 0)
                .value(OffsetDateTime.class, ChannelUpdateArchiveTimestampEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateArchiveTimestampEvent::getGuild)
                .channelValues(ChannelUpdateArchiveTimestampEvent::getChannel)
                .author(ChannelUpdateArchiveTimestampEvent::getGuild)
                .register();

        // Channel Update Auto Archive Duration Event
        EventRegistryFactory.builder(ChannelUpdateAutoArchiveDurationEvent.class)
                .name("Channel Auto Archive Duration Update Event")
                .patterns("[discord] channel auto[( |-)]archive duration (change|update)")
                .description("Fired when a thread channel's auto archive duration is changed.")
                .example("on channel auto-archive duration change:")
                .value(Timespan.class, evt -> evt.getOldValue() == null ? null : new Timespan(Timespan.TimePeriod.MINUTE, evt.getOldValue().getMinutes()), -1)
                .value(Timespan.class, evt -> evt.getNewValue() == null ? null : new Timespan(Timespan.TimePeriod.MINUTE, evt.getNewValue().getMinutes()), 0)
                .value(Timespan.class, evt -> evt.getNewValue() == null ? null : new Timespan(Timespan.TimePeriod.MINUTE, evt.getNewValue().getMinutes()), 1)
                .value(Guild.class, ChannelUpdateAutoArchiveDurationEvent::getGuild)
                .channelValues(ChannelUpdateAutoArchiveDurationEvent::getChannel)
                .author(ChannelUpdateAutoArchiveDurationEvent::getGuild)
                .register();

        // Channel Update Default Layout Event
        EventRegistryFactory.builder(ChannelUpdateDefaultLayoutEvent.class)
                .name("Channel Default Layout Update Event")
                .patterns("[discord] channel default layout (change|update)")
                .description("Fired when a forum channel's default layout is changed.")
                .example("on channel default layout change:")
                .value(String.class, evt -> evt.getOldValue().name().toLowerCase(), -1)
                .value(String.class, evt -> evt.getNewValue().name().toLowerCase(), 0)
                .value(String.class, evt -> evt.getNewValue().name().toLowerCase(), 1)
                .value(Guild.class, ChannelUpdateDefaultLayoutEvent::getGuild)
                .channelValues(ChannelUpdateDefaultLayoutEvent::getChannel)
                .author(ChannelUpdateDefaultLayoutEvent::getGuild)
                .register();

        // Channel Update Default Reaction Event
        EventRegistryFactory.builder(ChannelUpdateDefaultReactionEvent.class)
                .name("Channel Default Reaction Update Event")
                .patterns("[discord] channel default reaction (change|update)")
                .description("Fired when a forum channel's default reaction is changed.")
                .example("on channel default reaction change:")
                .value(EmojiUnion.class, ChannelUpdateDefaultReactionEvent::getOldValue, -1)
                .value(EmojiUnion.class, ChannelUpdateDefaultReactionEvent::getNewValue, 0)
                .value(EmojiUnion.class, ChannelUpdateDefaultReactionEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateDefaultReactionEvent::getGuild)
                .channelValues(ChannelUpdateDefaultReactionEvent::getChannel)
                .author(ChannelUpdateDefaultReactionEvent::getGuild)
                .register();

        // Channel Update Default Sort Order Event
        EventRegistryFactory.builder(ChannelUpdateDefaultSortOrderEvent.class)
                .name("Channel Default Sort Order Update Event")
                .patterns("[discord] channel default sort order (change|update)")
                .description("Fired when a forum channel's default sort order is changed.")
                .example("on channel default sort order change:")
                .value(String.class, evt -> evt.getOldValue().name().toLowerCase(), -1)
                .value(String.class, evt -> evt.getNewValue().name().toLowerCase(), 0)
                .value(String.class, evt -> evt.getNewValue().name().toLowerCase(), 1)
                .value(Guild.class, ChannelUpdateDefaultSortOrderEvent::getGuild)
                .channelValues(ChannelUpdateDefaultSortOrderEvent::getChannel)
                .author(ChannelUpdateDefaultSortOrderEvent::getGuild)
                .register();

        // Channel Update Default Thread Slowmode Event
        EventRegistryFactory.builder(ChannelUpdateDefaultThreadSlowmodeEvent.class)
                .name("Channel Default Thread Slowmode Update Event")
                .patterns("[discord] channel default thread slowmode (change|update)")
                .description("Fired when a forum channel's default thread slowmode is changed.")
                .example("on channel default thread slowmode change:")
                .value(Integer.class, ChannelUpdateDefaultThreadSlowmodeEvent::getOldValue, -1)
                .value(Integer.class, ChannelUpdateDefaultThreadSlowmodeEvent::getNewValue, 0)
                .value(Integer.class, ChannelUpdateDefaultThreadSlowmodeEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateDefaultThreadSlowmodeEvent::getGuild)
                .channelValues(ChannelUpdateDefaultThreadSlowmodeEvent::getChannel)
                .author(ChannelUpdateDefaultThreadSlowmodeEvent::getGuild)
                .register();

        // Channel Update Flags Event
        EventRegistryFactory.builder(ChannelUpdateFlagsEvent.class)
                .name("Channel Flags Update Event")
                .patterns("[discord] channel flags (change|update)")
                .description("Fired when a channel's flags are changed.")
                .example("on channel flags change:")
                .listExpression("[(current|new)] channel flags", String.class,
                        evt -> evt.getNewValue().stream()
                                .map(ChannelFlag::name)
                                .map(String::toLowerCase)
                                .toArray(String[]::new))
                .listExpression("(old|previous|past) channel flags", String.class,
                        evt -> evt.getOldValue().stream()
                                .map(ChannelFlag::name)
                                .map(String::toLowerCase)
                                .toArray(String[]::new))
                .value(Guild.class, ChannelUpdateFlagsEvent::getGuild)
                .channelValues(ChannelUpdateFlagsEvent::getChannel)
                .author(ChannelUpdateFlagsEvent::getGuild)
                .register();

        // Channel Update Invitable Event
        EventRegistryFactory.builder(ChannelUpdateInvitableEvent.class)
                .name("Channel Invitable Update Event")
                .patterns("[discord] channel invitable (change|update)")
                .description("Fired when a thread channel's invitable status is changed.")
                .example("on channel invitable change:")
                .value(Boolean.class, ChannelUpdateInvitableEvent::getOldValue, -1)
                .value(Boolean.class, ChannelUpdateInvitableEvent::getNewValue, 0)
                .value(Boolean.class, ChannelUpdateInvitableEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateInvitableEvent::getGuild)
                .channelValues(ChannelUpdateInvitableEvent::getChannel)
                .author(ChannelUpdateInvitableEvent::getGuild)
                .register();

        // Channel Update Locked Event
        EventRegistryFactory.builder(ChannelUpdateLockedEvent.class)
                .name("Channel Locked Update Event")
                .patterns("[discord] channel locked (change|update)")
                .description("Fired when a thread channel's locked status is changed.")
                .example("on channel locked change:")
                .value(Boolean.class, ChannelUpdateLockedEvent::getOldValue, -1)
                .value(Boolean.class, ChannelUpdateLockedEvent::getNewValue, 0)
                .value(Boolean.class, ChannelUpdateLockedEvent::getNewValue, 1)
                .value(Guild.class, ChannelUpdateLockedEvent::getGuild)
                .channelValues(ChannelUpdateLockedEvent::getChannel)
                .author(ChannelUpdateLockedEvent::getGuild)
                .register();
    }
}