package net.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;

@Name("All Guild Scheduled Events")
@Description("Returns all scheduled events of a guild.")
@Examples("all scheduled events of event-guild")
public class GuildScheduledEvents extends MultipleGuildProperty<ScheduledEvent> {

    static {
        register(GuildScheduledEvents.class,
                ScheduledEvent.class,
                "[all] scheduled events");
    }

    @Override
    public ScheduledEvent[] converting(Guild guild) {
        return guild.getScheduledEvents().toArray(new ScheduledEvent[0]);
    }
}
