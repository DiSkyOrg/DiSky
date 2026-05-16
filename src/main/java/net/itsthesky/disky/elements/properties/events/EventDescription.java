package net.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Description of Scheduled Event")
@Description({"Get the description of a scheduled event.",
        "This can be null if the event has no description."})
@Since("4.29.0")
public class EventDescription extends SimplePropertyExpression<ScheduledEvent, String> {

    static {
        register(
                EventDescription.class,
                String.class,
                "scheduled [event] description",
                "scheduledevent"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "scheduled event description";
    }

    @Override
    public @Nullable String convert(ScheduledEvent scheduledEvent) {
        return scheduledEvent.getDescription();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
