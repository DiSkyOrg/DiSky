package net.itsthesky.disky.elements.events.rework;

import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateCoverImageEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateEndTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateLocationEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStartTimeEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.ScheduledEventUpdateStatusEvent;
import net.itsthesky.disky.api.events.rework.EventCategory;
import net.itsthesky.disky.api.events.rework.EventRegistryFactory;
import net.itsthesky.disky.core.SkriptUtils;

@EventCategory(name = "Scheduled Event Events", description = {
        "Events related to Discord scheduled events (also known as 'guild events' in the UI).",
        "These events are fired when a scheduled event is created, updated, deleted,",
        "or when a user subscribes/unsubscribes to one.",
        "",
        "Requires the SCHEDULED_EVENTS intent and the SCHEDULED_EVENTS cache flag to be enabled.",
        "Note: JDABuilder.createDefault(String) and JDABuilder.createLight(String) disable these by default."
})
public class ScheduledEvents {

    static {
        // Scheduled Event Create Event
        // Fired when a new scheduled event is created in a guild.
        EventRegistryFactory.builder(ScheduledEventCreateEvent.class)
                .name("Scheduled Event Create")
                .patterns("[discord] scheduled[( |-)]event creat(e|ion)")
                .description("Fired when a scheduled event is created in a guild. Can be used to get the scheduled event and the guild it was created in.")
                .example("on scheduled event creation:\n    broadcast \"A new scheduled event '%event-scheduledevent's name%' was created in %event-guild%\"")
                .value(ScheduledEvent.class, ScheduledEventCreateEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventCreateEvent::getGuild, 0)
                .author(ScheduledEventCreateEvent::getGuild)
                .register();

        // Scheduled Event Delete Event
        // Fired when a scheduled event is deleted from a guild.
        EventRegistryFactory.builder(ScheduledEventDeleteEvent.class)
                .name("Scheduled Event Delete")
                .patterns("[discord] scheduled[( |-)]event delet(e|ion)")
                .description("Fired when a scheduled event is deleted from a guild. Can be used to get the scheduled event and the guild it was deleted from.")
                .example("on scheduled event deletion:\n    broadcast \"Scheduled event '%event-scheduledevent's name%' was deleted from %event-guild%\"")
                .value(ScheduledEvent.class, ScheduledEventDeleteEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventDeleteEvent::getGuild, 0)
                .author(ScheduledEventDeleteEvent::getGuild)
                .register();

        // Scheduled Event User Add Event
        // Fired when a user subscribes (RSVPs) to a scheduled event.
        EventRegistryFactory.builder(ScheduledEventUserAddEvent.class)
                .name("Scheduled Event User Add")
                .patterns("[discord] scheduled[( |-)]event user (add|subscribe|join)")
                .description("Fired when a user subscribes (RSVPs as 'interested') to a scheduled event. The user/member may be null if not cached; use the 'subscriber' rest value to retrieve it from Discord.")
                .example("on scheduled event user add:\n    broadcast \"%event-user% is now interested in %event-scheduledevent's name%\"")
                .value(ScheduledEvent.class, ScheduledEventUserAddEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUserAddEvent::getGuild, 0)
                .value(User.class, ScheduledEventUserAddEvent::getUser, 0)
                .value(Member.class, ScheduledEventUserAddEvent::getMember, 0)
                .author(ScheduledEventUserAddEvent::getGuild)
                .restValue("subscriber", ScheduledEventUserAddEvent::retrieveUser)
                .restValue("member", ScheduledEventUserAddEvent::retrieveMember)
                .register();

        // Scheduled Event User Remove Event
        // Fired when a user unsubscribes from a scheduled event.
        EventRegistryFactory.builder(ScheduledEventUserRemoveEvent.class)
                .name("Scheduled Event User Remove")
                .patterns("[discord] scheduled[( |-)]event user (remove|unsubscribe|leave)")
                .description("Fired when a user unsubscribes from a scheduled event. The user/member may be null if not cached; use the 'subscriber' rest value to retrieve it from Discord.")
                .example("on scheduled event user remove:\n    broadcast \"%event-user% is no longer interested in %event-scheduledevent's name%\"")
                .value(ScheduledEvent.class, ScheduledEventUserRemoveEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUserRemoveEvent::getGuild, 0)
                .value(User.class, ScheduledEventUserRemoveEvent::getUser, 0)
                .value(Member.class, ScheduledEventUserRemoveEvent::getMember, 0)
                .author(ScheduledEventUserRemoveEvent::getGuild)
                .restValue("subscriber", ScheduledEventUserRemoveEvent::retrieveUser)
                .restValue("member", ScheduledEventUserRemoveEvent::retrieveMember)
                .register();

        // Scheduled Event Update Name Event
        // Fired when the name of a scheduled event is changed.
        EventRegistryFactory.builder(ScheduledEventUpdateNameEvent.class)
                .name("Scheduled Event Name Update")
                .patterns("[discord] scheduled[( |-)]event name (change|update)")
                .description("Fired when a scheduled event's name is changed.")
                .example("on scheduled event name change:\n    broadcast \"Scheduled event renamed from '%past scheduled event name%' to '%new scheduled event name%'\"")
                .customTimedExpressions("scheduled [event] name", String.class,
                        ScheduledEventUpdateNameEvent::getNewValue,
                        ScheduledEventUpdateNameEvent::getOldValue)
                .value(ScheduledEvent.class, ScheduledEventUpdateNameEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateNameEvent::getGuild, 0)
                .author(ScheduledEventUpdateNameEvent::getGuild)
                .register();

        // Scheduled Event Update Description Event
        // Fired when the description of a scheduled event is changed.
        EventRegistryFactory.builder(ScheduledEventUpdateDescriptionEvent.class)
                .name("Scheduled Event Description Update")
                .patterns("[discord] scheduled[( |-)]event description (change|update)")
                .description("Fired when a scheduled event's description is changed.")
                .example("on scheduled event description change:\n    broadcast \"Description of %event-scheduledevent's name% changed\"")
                .customTimedExpressions("scheduled [event] description", String.class,
                        ScheduledEventUpdateDescriptionEvent::getNewValue,
                        ScheduledEventUpdateDescriptionEvent::getOldValue)
                .value(ScheduledEvent.class, ScheduledEventUpdateDescriptionEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateDescriptionEvent::getGuild, 0)
                .author(ScheduledEventUpdateDescriptionEvent::getGuild)
                .register();

        // Scheduled Event Update Location Event
        // Fired when the location of a scheduled event is changed. For external events
        // this is a free-form string; for channel-bound events it is the channel ID as a string.
        EventRegistryFactory.builder(ScheduledEventUpdateLocationEvent.class)
                .name("Scheduled Event Location Update")
                .patterns("[discord] scheduled[( |-)]event location (change|update)")
                .description(
                        "Fired when a scheduled event's location is changed.",
                        "For external events this is a free-form string; for channel-bound events it is the channel ID as a string.",
                        "Use 'event-channel' to access the new channel if the event is bound to a channel."
                )
                .example("on scheduled event location change:\n    broadcast \"%event-scheduledevent's name% moved from '%past scheduled event location%' to '%new scheduled event location%'\"")
                .customTimedExpressions("scheduled [event] location", String.class,
                        ScheduledEventUpdateLocationEvent::getNewValue,
                        ScheduledEventUpdateLocationEvent::getOldValue)
                .value(ScheduledEvent.class, ScheduledEventUpdateLocationEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateLocationEvent::getGuild, 0)
                .value(Channel.class, evt -> evt.getScheduledEvent().getChannel())
                .author(ScheduledEventUpdateLocationEvent::getGuild)
                .register();

        // Scheduled Event Update Start Time Event
        // Fired when the start time of a scheduled event is changed.
        EventRegistryFactory.builder(ScheduledEventUpdateStartTimeEvent.class)
                .name("Scheduled Event Start Time Update")
                .patterns("[discord] scheduled[( |-)]event start[( |-)]time (change|update)")
                .description("Fired when a scheduled event's start time is changed.")
                .example("on scheduled event start time change:\n    broadcast \"%event-scheduledevent's name% will now start at %new scheduled event start time%\"")
                .customTimedExpressions("scheduled [event] start[ ]time", Date.class,
                        evt -> SkriptUtils.convertDateTime(evt.getNewValue()),
                        evt -> SkriptUtils.convertDateTime(evt.getOldValue()))
                .value(ScheduledEvent.class, ScheduledEventUpdateStartTimeEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateStartTimeEvent::getGuild, 0)
                .author(ScheduledEventUpdateStartTimeEvent::getGuild)
                .register();

        // Scheduled Event Update End Time Event
        // Fired when the end time of a scheduled event is changed.
        // Note: the end time may be null (events without a defined end time).
        EventRegistryFactory.builder(ScheduledEventUpdateEndTimeEvent.class)
                .name("Scheduled Event End Time Update")
                .patterns("[discord] scheduled[( |-)]event end[( |-)]time (change|update)")
                .description("Fired when a scheduled event's end time is changed. The old or new value may be null if the event has no defined end time.")
                .example("on scheduled event end time change:\n    broadcast \"%event-scheduledevent's name% will now end at %new scheduled event end time%\"")
                .customTimedExpressions("scheduled [event] end[ ]time", Date.class,
                        evt -> evt.getNewValue() == null ? null : SkriptUtils.convertDateTime(evt.getNewValue()),
                        evt -> evt.getOldValue() == null ? null : SkriptUtils.convertDateTime(evt.getOldValue()))
                .value(ScheduledEvent.class, ScheduledEventUpdateEndTimeEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateEndTimeEvent::getGuild, 0)
                .author(ScheduledEventUpdateEndTimeEvent::getGuild)
                .register();

        // Scheduled Event Update Status Event
        // Fired when the status (scheduled / active / completed / cancelled) of a scheduled event changes.
        // This is the most useful update event: it tells you when an event actually starts and ends.
        EventRegistryFactory.builder(ScheduledEventUpdateStatusEvent.class)
                .name("Scheduled Event Status Update")
                .patterns("[discord] scheduled[( |-)]event status (change|update)")
                .description(
                        "Fired when a scheduled event's status changes between: scheduled, active, completed, cancelled.",
                        "This is how you detect when an event actually starts (status becomes 'active') or ends (status becomes 'completed')."
                )
                .example("on scheduled event status change:\n    if new scheduled event status is \"active\":\n        broadcast \"%event-scheduledevent's name% has started!\"")
                .customTimedExpressions("scheduled [event] status", String.class,
                        evt -> evt.getNewValue() == null ? null : evt.getNewValue().name().toLowerCase().replace("_", " "),
                        evt -> evt.getOldValue() == null ? null : evt.getOldValue().name().toLowerCase().replace("_", " "))
                .value(ScheduledEvent.class, ScheduledEventUpdateStatusEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateStatusEvent::getGuild, 0)
                .author(ScheduledEventUpdateStatusEvent::getGuild)
                .register();

        // Scheduled Event Update Cover Image Event
        // Fired when the cover image of a scheduled event is changed.
        // Both old and new values are URLs to the image (or null when no image is set).
        EventRegistryFactory.builder(ScheduledEventUpdateCoverImageEvent.class)
                .name("Scheduled Event Cover Image Update")
                .patterns("[discord] scheduled[( |-)]event (cover[( |-)])?image (change|update)")
                .description("Fired when a scheduled event's cover image is changed. The old/new values are image URLs (or null if no image is set).")
                .example("on scheduled event cover image change:\n    broadcast \"Cover image of %event-scheduledevent's name% changed to %new scheduled event cover image%\"")
                .customTimedExpressions("scheduled [event] (cover[ ])?image", String.class,
                        ScheduledEventUpdateCoverImageEvent::getNewImageUrl,
                        ScheduledEventUpdateCoverImageEvent::getOldImageUrl)
                .value(ScheduledEvent.class, ScheduledEventUpdateCoverImageEvent::getScheduledEvent, 0)
                .value(Guild.class, ScheduledEventUpdateCoverImageEvent::getGuild, 0)
                .author(ScheduledEventUpdateCoverImageEvent::getGuild)
                .register();
    }
}