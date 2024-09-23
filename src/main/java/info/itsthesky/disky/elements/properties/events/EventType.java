package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Type of Scheduled Event")
@Description({"Get the type of a scheduled event.",
"It can either be 'voice/stage instance' or 'external' according to the type of the event."})
@Since("4.8.0")
public class EventType extends SimplePropertyExpression<ScheduledEvent, String> {

	static {
		register(
				EventType.class,
				String.class,
				"scheduled [event] type",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event type";
	}

	@Override
	public @Nullable String convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getType().name().toLowerCase().replace("_", " ");
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
