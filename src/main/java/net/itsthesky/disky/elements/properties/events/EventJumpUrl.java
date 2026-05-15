package net.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Jump URL of Scheduled Event")
@Description("Get the jump URL of a scheduled event.")
@Since("4.29.0")
public class EventJumpUrl extends SimplePropertyExpression<ScheduledEvent, String> {

    static {
        register(
                EventJumpUrl.class,
                String.class,
                "scheduled [event] jump url",
                "scheduledevent"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "scheduled event jump url";
    }

    @Override
    public @Nullable String convert(ScheduledEvent scheduledEvent) {
        return scheduledEvent.getJumpUrl();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
