package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelFlag;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.forums.BaseForumTag;
import net.dv8tion.jda.api.entities.channel.forums.ForumTag;
import net.dv8tion.jda.api.entities.emoji.EmojiUnion;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.itsthesky.disky.api.emojis.Emote;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

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
                .example("on channel name change:\n\tbroadcast \"Channel %event-channel% was renamed from %previous channel name% to %current channel name%\"")
                .customTimedExpressions("[channel] name", String.class,
                        ChannelUpdateNameEvent::getNewValue, ChannelUpdateNameEvent::getOldValue)
                .value(Guild.class, ChannelUpdateNameEvent::getGuild)
                .channelValues(ChannelUpdateNameEvent::getChannel)
                .author(ChannelUpdateNameEvent::getGuild)
                .register();

        // Channel Update Topic Event
        EventRegistryFactory.builder(ChannelUpdateTopicEvent.class)
                .name("Channel Topic Update Event")
                .patterns("[discord] channel topic (change|update)")
                .description("Fired when a channel's topic is changed.")
                .example("on channel topic change:\n\tbroadcast \"Channel %event-channel% had its topic changed from '%previous channel topic%' to '%current channel topic%'\"")
                .customTimedExpressions("[channel] topic", String.class,
                        ChannelUpdateTopicEvent::getNewValue, ChannelUpdateTopicEvent::getOldValue)
                .value(Guild.class, ChannelUpdateTopicEvent::getGuild)
                .channelValues(ChannelUpdateTopicEvent::getChannel)
                .author(ChannelUpdateTopicEvent::getGuild)
                .register();

        // Channel Update NSFW Event
        EventRegistryFactory.builder(ChannelUpdateNSFWEvent.class)
                .name("Channel NSFW Update Event")
                .patterns("[discord] channel nsfw (change|update)")
                .description("Fired when a channel's NSFW status is changed.")
                .example("on channel nsfw change:\n\tbroadcast \"Channel %event-channel% NSFW status changed from %past nsfw state% to %current nsfw state%\"")
                .customTimedExpressions("[channel] nsfw state", Boolean.class,
                        ChannelUpdateNSFWEvent::getNewValue, ChannelUpdateNSFWEvent::getOldValue)
                .value(Guild.class, ChannelUpdateNSFWEvent::getGuild)
                .channelValues(ChannelUpdateNSFWEvent::getChannel)
                .author(ChannelUpdateNSFWEvent::getGuild)
                .register();

        // Channel Update Position Event
        EventRegistryFactory.builder(ChannelUpdatePositionEvent.class)
                .name("Channel Position Update Event")
                .patterns("[discord] channel position (change|update)")
                .description("Fired when a channel's position is changed.")
                .example("on channel position change:\n\tbroadcast \"Channel %event-channel% position changed from %past channel position% to %current channel position%\"")
                .customTimedExpressions("[chaannel] position", Integer.class,
                        ChannelUpdatePositionEvent::getNewValue, ChannelUpdatePositionEvent::getOldValue)
                .value(Guild.class, ChannelUpdatePositionEvent::getGuild)
                .channelValues(ChannelUpdatePositionEvent::getChannel)
                .author(ChannelUpdatePositionEvent::getGuild)
                .register();

        // Channel Update Parent Event
        EventRegistryFactory.builder(ChannelUpdateParentEvent.class)
                .name("Channel Parent Update Event")
                .patterns("[discord] channel parent (change|update)")
                .description("Fired when a channel's parent category is changed.")
                .example("on channel parent change:\n\tbroadcast \"Channel %event-channel% was moved from %past parent% to %parent%\"")
                .customTimedExpressions("[category] parent", Category.class,
                        ChannelUpdateParentEvent::getNewValue, ChannelUpdateParentEvent::getOldValue)
                .value(Guild.class, ChannelUpdateParentEvent::getGuild)
                .channelValues(ChannelUpdateParentEvent::getChannel)
                .author(ChannelUpdateParentEvent::getGuild)
                .register();

        // Channel Update Slowmode Event
        EventRegistryFactory.builder(ChannelUpdateSlowmodeEvent.class)
                .name("Channel Slowmode Update Event")
                .patterns("[discord] channel slowmode (change|update)")
                .description("Fired when a channel's slowmode setting is changed.")
                .example("on channel slowmode change:\n\tbroadcast \"Channel %event-channel% slowmode changed from %past channel slowmode% to %new channel slowmode% seconds\"")
                .customTimedExpressions("[channel] slowmode", Number.class,
                        ChannelUpdateSlowmodeEvent::getNewValue, ChannelUpdateSlowmodeEvent::getOldValue)
                .value(Guild.class, ChannelUpdateSlowmodeEvent::getGuild)
                .channelValues(ChannelUpdateSlowmodeEvent::getChannel)
                .author(ChannelUpdateSlowmodeEvent::getGuild)
                .register();

        // Channel Update Type Event
        EventRegistryFactory.builder(ChannelUpdateTypeEvent.class)
                .name("Channel Type Update Event")
                .patterns("[discord] channel type (change|update)")
                .description("Fired when a channel's type is changed.")
                .example("on channel type change:\n\tbroadcast \"Channel %event-channel% type changed from %past channel type% to %current channel type%\"")
                .customTimedExpressions("[channel] type", ChannelType.class,
                        ChannelUpdateTypeEvent::getNewValue, ChannelUpdateTypeEvent::getOldValue)
                .value(Guild.class, ChannelUpdateTypeEvent::getGuild)
                .channelValues(ChannelUpdateTypeEvent::getChannel)
                .author(ChannelUpdateTypeEvent::getGuild)
                .register();

        // Channel Update User Limit Event
        EventRegistryFactory.builder(ChannelUpdateUserLimitEvent.class)
                .name("Channel User Limit Update Event")
                .patterns("[discord] channel user limit (change|update)")
                .description("Fired when a voice channel's user limit is changed.")
                .example("on channel user limit change:\n\tbroadcast \"Channel %event-channel% user limit changed from %past user limit% to %current user limit%\"")
                .customTimedExpressions("[channel] user limit", Number.class,
                        ChannelUpdateUserLimitEvent::getNewValue, ChannelUpdateUserLimitEvent::getOldValue)
                .value(Guild.class, ChannelUpdateUserLimitEvent::getGuild)
                .channelValues(ChannelUpdateUserLimitEvent::getChannel)
                .author(ChannelUpdateUserLimitEvent::getGuild)
                .register();

        // Channel Update Bitrate Event
        EventRegistryFactory.builder(ChannelUpdateBitrateEvent.class)
                .name("Channel Bitrate Update Event")
                .patterns("[discord] channel bitrate (change|update)")
                .description("Fired when a voice channel's bitrate is changed.")
                .example("on channel bitrate change:\n\tbroadcast \"Channel %event-channel% bitrate changed from %past bitrate% to %current bitrate%\"")
                .customTimedExpressions("[channel] bitrate", Number.class,
                        ChannelUpdateBitrateEvent::getNewValue, ChannelUpdateBitrateEvent::getOldValue)
                .value(Guild.class, ChannelUpdateBitrateEvent::getGuild)
                .channelValues(ChannelUpdateBitrateEvent::getChannel)
                .author(ChannelUpdateBitrateEvent::getGuild)
                .register();

        // Channel Update Region Event
        EventRegistryFactory.builder(ChannelUpdateRegionEvent.class)
                .name("Channel Region Update Event")
                .patterns("[discord] channel region (change|update)")
                .description("Fired when a voice channel's region is changed.")
                .example("on channel region change:\n\tbroadcast \"Channel %event-channel% region changed from %past channel region% to %current channel region%\"")

                .customTimedExpressions("[channel] region", String.class,
                        evt -> evt.getNewValue() == null ? null : evt.getNewValue().name().toLowerCase(),
                        evt -> evt.getOldValue() == null ? null : evt.getOldValue().name().toLowerCase())

                .value(Guild.class, ChannelUpdateRegionEvent::getGuild)
                .channelValues(ChannelUpdateRegionEvent::getChannel)
                .author(ChannelUpdateRegionEvent::getGuild)
                .register();

        // Channel Update Voice Status Event
        EventRegistryFactory.builder(ChannelUpdateVoiceStatusEvent.class)
                .name("Channel Voice Status Update Event")
                .patterns("[discord] channel voice status (change|update)")
                .description("Fired when a voice channel's status (video or voice) is changed.")
                .example("on channel voice status change:\n\tbroadcast \"Channel %event-channel% voice status changed from %past channel voice status% to %current channel voice status%\"")
                .customTimedExpressions("[channel] voice status", String.class,
                        GenericChannelUpdateEvent::getNewValue,
                        GenericChannelUpdateEvent::getOldValue)
                .value(Guild.class, ChannelUpdateVoiceStatusEvent::getGuild)
                .channelValues(ChannelUpdateVoiceStatusEvent::getChannel)
                .author(ChannelUpdateVoiceStatusEvent::getGuild)
                .register();

        // Channel Update Applied Tags Event
        EventRegistryFactory.builder(ChannelUpdateAppliedTagsEvent.class)
                .name("Channel Applied Tags Update Event")
                .patterns("[discord] channel [applied] tags (change|update)")
                .description("Fired when a forum channel's applied tags are changed.")
                .example("on channel tags change:\n\tbroadcast \"Channel %event-channel% applied tags changed from %old applied tags% to %new applied tags%\"")
                .customTimedListExpressions("[applied] tags", ForumTag.class,
                        evt -> evt.getAddedTags().toArray(ForumTag[]::new),
                        evt -> evt.getRemovedTags().toArray(ForumTag[]::new))
                .value(Guild.class, ChannelUpdateAppliedTagsEvent::getGuild)
                .channelValues(ChannelUpdateAppliedTagsEvent::getChannel)
                .author(ChannelUpdateAppliedTagsEvent::getGuild)
                .register();

        // Channel Update Archived Event
        EventRegistryFactory.builder(ChannelUpdateArchivedEvent.class)
                .name("Channel Archived Update Event")
                .patterns("[discord] channel archived (change|update)")
                .description("Fired when a thread channel's archived status is changed.")
                .example("on channel archived change:\n\tbroadcast \"Channel %event-channel% archived status changed from %past channel archived state% to %current channel archived state%\"")
                .customTimedExpressions("[channel] archive[d] state", Boolean.class,
                        GenericChannelUpdateEvent::getNewValue, GenericChannelUpdateEvent::getOldValue)
                .value(Guild.class, ChannelUpdateArchivedEvent::getGuild)
                .channelValues(ChannelUpdateArchivedEvent::getChannel)
                .author(ChannelUpdateArchivedEvent::getGuild)
                .register();

        // Channel Update Archive Timestamp Event
        EventRegistryFactory.builder(ChannelUpdateArchiveTimestampEvent.class)
                .name("Channel Archive Timestamp/Date Update Event")
                .patterns("[discord] channel archive (timestam|date) (change|update)")
                .description("Fired when a thread channel's archive timestamp is changed.")
                .example("on channel archive timestamp change:\n\tbroadcast \"Channel %event-channel% archive timestamp changed from %past channel archive timestamp% to %current channel archive timestamp%\"")
                .customTimedExpressions("[channel] archive (timestamp|date)", Date.class,
                        evt -> SkriptUtils.convertDateTime(evt.getNewValue()),
                        evt -> SkriptUtils.convertDateTime(evt.getOldValue()))
                .value(Guild.class, ChannelUpdateArchiveTimestampEvent::getGuild)
                .channelValues(ChannelUpdateArchiveTimestampEvent::getChannel)
                .author(ChannelUpdateArchiveTimestampEvent::getGuild)
                .register();

        // Channel Update Auto Archive Duration Event
        EventRegistryFactory.builder(ChannelUpdateAutoArchiveDurationEvent.class)
                .name("Channel Auto Archive Duration Update Event")
                .patterns("[discord] channel auto[( |-)]archive duration (change|update)")
                .description("Fired when a thread channel's auto archive duration is changed.")
                .example("on channel auto-archive duration change:\n\tbroadcast \"Channel %event-channel% auto archive duration changed from %past channel auto archive duration% to %current channel auto archive duration%\"")
                .customTimedExpressions("[channel] auto archive duration", Timespan.class,
                        evt -> evt.getNewValue() == null ? null : new Timespan(Timespan.TimePeriod.MINUTE, evt.getNewValue().getMinutes()),
                        evt -> evt.getOldValue() == null ? null : new Timespan(Timespan.TimePeriod.MINUTE, evt.getOldValue().getMinutes()))
                .value(Guild.class, ChannelUpdateAutoArchiveDurationEvent::getGuild)
                .channelValues(ChannelUpdateAutoArchiveDurationEvent::getChannel)
                .author(ChannelUpdateAutoArchiveDurationEvent::getGuild)
                .register();

        // Channel Update Default Layout Event
        EventRegistryFactory.builder(ChannelUpdateDefaultLayoutEvent.class)
                .name("Channel Default Layout Update Event")
                .patterns("[discord] channel default layout (change|update)")
                .description("Fired when a forum channel's default layout is changed.")
                .example("on channel default layout change:\n\tbroadcast \"Channel %event-channel% default layout changed from %old default layout% to %new default layout%\"")
                .customTimedExpressions("[channel] default layout", String.class,
                        evt -> evt.getNewValue().name().toLowerCase(),
                        evt -> evt.getOldValue().name().toLowerCase())
                .value(Guild.class, ChannelUpdateDefaultLayoutEvent::getGuild)
                .channelValues(ChannelUpdateDefaultLayoutEvent::getChannel)
                .author(ChannelUpdateDefaultLayoutEvent::getGuild)
                .register();

        // Channel Update Default Reaction Event
        EventRegistryFactory.builder(ChannelUpdateDefaultReactionEvent.class)
                .name("Channel Default Reaction Update Event")
                .patterns("[discord] channel default reaction (change|update)")
                .description("Fired when a forum channel's default reaction is changed.")
                .example("on channel default reaction change:\n\tbroadcast \"Channel %event-channel% default reaction changed from %old default reaction% to %new default reaction%\"")
                .customTimedExpressions("[channel] default reaction", Emote.class,
                        evt -> Emote.fromUnion(evt.getNewValue()),
                        evt -> Emote.fromUnion(evt.getOldValue()))
                .value(Guild.class, ChannelUpdateDefaultReactionEvent::getGuild)
                .channelValues(ChannelUpdateDefaultReactionEvent::getChannel)
                .author(ChannelUpdateDefaultReactionEvent::getGuild)
                .register();

        // Channel Update Default Sort Order Event
        EventRegistryFactory.builder(ChannelUpdateDefaultSortOrderEvent.class)
                .name("Channel Default Sort Order Update Event")
                .patterns("[discord] channel default sort order (change|update)")
                .description("Fired when a forum channel's default sort order is changed.")
                .example("on channel default sort order change:\n\tbroadcast \"Channel %event-channel% default sort order changed from %old default sort order% to %new default sort order%\"")
                .customTimedExpressions("[channel] default sort order", String.class,
                        evt -> evt.getNewValue().name().toLowerCase(),
                        evt -> evt.getOldValue().name().toLowerCase())
                .value(Guild.class, ChannelUpdateDefaultSortOrderEvent::getGuild)
                .channelValues(ChannelUpdateDefaultSortOrderEvent::getChannel)
                .author(ChannelUpdateDefaultSortOrderEvent::getGuild)
                .register();

        // Channel Update Default Thread Slowmode Event
        EventRegistryFactory.builder(ChannelUpdateDefaultThreadSlowmodeEvent.class)
                .name("Channel Default Thread Slowmode Update Event")
                .patterns("[discord] channel default thread slowmode (change|update)")
                .description("Fired when a forum channel's default thread slowmode is changed.")
                .example("on channel default thread slowmode change:\n\tbroadcast \"Channel %event-channel% default thread slowmode changed from %old default thread slowmode% to %new default thread slowmode%\"")
                .customTimedExpressions("[channel] default thread slowmode", Number.class,
                        ChannelUpdateDefaultThreadSlowmodeEvent::getNewValue, ChannelUpdateDefaultThreadSlowmodeEvent::getOldValue)
                .value(Guild.class, ChannelUpdateDefaultThreadSlowmodeEvent::getGuild)
                .channelValues(ChannelUpdateDefaultThreadSlowmodeEvent::getChannel)
                .author(ChannelUpdateDefaultThreadSlowmodeEvent::getGuild)
                .register();

        // Channel Update Flags Event
        EventRegistryFactory.builder(ChannelUpdateFlagsEvent.class)
                .name("Channel Flags Update Event")
                .patterns("[discord] channel flags (change|update)")
                .description("Fired when a channel's flags are changed.")
                .example("on channel flags change:\n\tbroadcast \"Channel %event-channel% flags changed from %old channel flags% to %new channel flags%\"")
                .customTimedListExpressions("[channel] flags", String.class,
                        evt -> evt.getNewValue().stream()
                                .map(ChannelFlag::name)
                                .map(String::toLowerCase)
                                .toArray(String[]::new),
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
                .example("on channel invitable change:\n\tbroadcast \"Channel %event-channel% invitable status changed from %past channel invitable state% to %current channel invitable state%\"")
                .customTimedExpressions("[channel] invitable [state]", Boolean.class,
                        ChannelUpdateInvitableEvent::getNewValue, ChannelUpdateInvitableEvent::getOldValue)
                .value(Guild.class, ChannelUpdateInvitableEvent::getGuild)
                .channelValues(ChannelUpdateInvitableEvent::getChannel)
                .author(ChannelUpdateInvitableEvent::getGuild)
                .register();

        // Channel Update Locked Event
        EventRegistryFactory.builder(ChannelUpdateLockedEvent.class)
                .name("Channel Locked Update Event")
                .patterns("[discord] channel locked (change|update)")
                .description("Fired when a thread channel's locked status is changed.")
                .example("on channel locked change:\n\tbroadcast \"Channel %event-channel% locked status changed from %past channel locked state% to %current channel locked state%\"")
                .customTimedExpressions("[channel] locked [state]", Boolean.class,
                        ChannelUpdateLockedEvent::getNewValue, ChannelUpdateLockedEvent::getOldValue)
                .value(Guild.class, ChannelUpdateLockedEvent::getGuild)
                .channelValues(ChannelUpdateLockedEvent::getChannel)
                .author(ChannelUpdateLockedEvent::getGuild)
                .register();
    }
}