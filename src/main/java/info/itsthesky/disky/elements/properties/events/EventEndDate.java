package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("End date of Scheduled Event")
@Description({"Get the end date of a scheduled event.",
"Can be null if the event is made from a channel and not an external place."})
@Since("4.8.0")
public class EventEndDate extends SimpleScheduledEventExpression<Date> {

	static {
		register(
				EventEndDate.class,
				Date.class,
				"scheduled [event] end date",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event end date";
	}

	@Override
	public @Nullable Date convert(ScheduledEvent scheduledEvent) {
		if (scheduledEvent.getEndTime() == null)
			return null;

		return new Date(scheduledEvent.getEndTime().toInstant().toEpochMilli());
	}

	@Override
	public RestAction<?> change(ScheduledEvent entity, Object[] delta) {
		final Date date = (Date) delta[0];
		if (date == null)
			return null;

		return entity.getManager().setEndTime(new java.util.Date(date.getTime()).toInstant());
	}

	@Override
	public @NotNull Class<? extends Date> getReturnType() {
		return Date.class;
	}

}
