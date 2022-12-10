package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Cover of Scheduled Event")
@Description({"Get the cover of a scheduled event.",
"Links to a potentially heavily compressed image. You can append a size parameter to the URL if needed. Example: ?size=4096",
"This can returns null if no cover is set for the event."})
@Since("4.8.0")
public class EventCover extends SimplePropertyExpression<ScheduledEvent, String> {

	static {
		register(
				EventCover.class,
				String.class,
				"scheduled [event] cover",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event cover";
	}

	@Override
	public @Nullable String convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getImageUrl();
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
