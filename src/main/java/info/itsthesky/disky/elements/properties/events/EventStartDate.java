package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Start date of Scheduled Event")
@Description({"Get the start date of a scheduled event. Cannot be null."})
@Since("4.8.0")
public class EventStartDate extends SimpleScheduledEventExpression<Date> {

	static {
		register(
				EventStartDate.class,
				Date.class,
				"scheduled [event] [start] date",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event date";
	}

	@Override
	public @Nullable Date convert(ScheduledEvent scheduledEvent) {
		return new Date(scheduledEvent.getStartTime().toInstant().toEpochMilli());
	}

	@Override
	public RestAction<?> change(ScheduledEvent entity, Object[] delta) {
		final Date date = (Date) delta[0];
		if (date == null)
			return null;

		return entity.getManager().setStartTime(new java.util.Date(date.getTime()).toInstant());
	}

	@Override
	public @NotNull Class<? extends Date> getReturnType() {
		return Date.class;
	}

}
