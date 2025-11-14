package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.SeeAlso;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;

@Name("Get Scheduled Event")
@Description({"Get a scheduled event from a guild using its unique ID.",
        "Scheduled events are global on discord, means different scheduled events cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("scheduled event with id \"000\"")
@Since("4.0.0")
@SeeAlso(ScheduledEvent.class)
public class GetScheduledEvent extends BaseGetterExpression<ScheduledEvent> {

    static {
        register(GetScheduledEvent.class,
                ScheduledEvent.class,
                "scheduled event");
    }

    @Override
    protected ScheduledEvent get(String id, Bot bot) {
        return bot.getInstance().getScheduledEventById(id);
    }

    @Override
    public String getCodeName() {
        return "scheduled event";
    }

    @Override
    public @NotNull Class<? extends ScheduledEvent> getReturnType() {
        return ScheduledEvent.class;
    }
}
