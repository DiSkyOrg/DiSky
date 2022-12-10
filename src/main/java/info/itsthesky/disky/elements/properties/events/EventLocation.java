package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Location of Scheduled Event")
@Description({"Get the location of a scheduled event.",
"Returns the specified place if the event is external, or the audio channel's ID."})
@Since("4.8.0")
public class EventLocation extends SimplePropertyExpression<ScheduledEvent, String> {

	static {
		register(
				EventLocation.class,
				String.class,
				"scheduled [event] location",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event location";
	}

	@Override
	public @Nullable String convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getLocation();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
