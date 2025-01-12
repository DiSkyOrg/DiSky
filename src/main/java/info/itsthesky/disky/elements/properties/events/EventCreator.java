package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Creator of Scheduled Event")
@Description({"Get the creator of a scheduled event.",
"May return none if user has deleted their account, the User object is not cached or the event was created before Discord started keeping track of event creators on October 21st, 2021"})
@Since("4.8.0")
public class EventCreator extends SimplePropertyExpression<ScheduledEvent, User> {

	static {
		register(
				EventCreator.class,
				User.class,
				"scheduled [event] creator",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event creator";
	}

	@Override
	public @Nullable User convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getCreator();
	}

	@Override
	public @NotNull Class<? extends User> getReturnType() {
		return User.class;
	}

}
