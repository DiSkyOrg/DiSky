package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("End date of Scheduled Event")
@Description({"Get the end date of a scheduled event.",
"Can be null if the event is made from a channel and not an external place."})
@Since("4.8.0")
public class EventEndDate extends SimplePropertyExpression<ScheduledEvent, Date> {

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
	public @NotNull Class<? extends Date> getReturnType() {
		return Date.class;
	}

}
