package net.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Status of Scheduled Event")
@Description({"Get the status of a scheduled event between:",
		"- Scheduled",
		"- Active",
		"- Completed",
		"- Cancelled"})
@Since("4.8.0")
public class EventStatus extends SimpleScheduledEventExpression<String> {

	static {
		register(
				EventStatus.class,
				String.class,
				"scheduled [event] status",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event status";
	}

	@Override
	public @Nullable String convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getStatus().name().toLowerCase().replace("_", " ");
	}

	@Override
	public RestAction<?> change(ScheduledEvent entity, Object[] delta) {
		final String raw = delta[0] == null ? null : ((String) delta[0]).toUpperCase().replace(" ", "_");
		final ScheduledEvent.Status status = ScheduledEvent.Status.valueOf(raw);

		return entity.getManager().setStatus(status);
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
