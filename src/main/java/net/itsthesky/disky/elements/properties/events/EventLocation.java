package net.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Location of Scheduled Event")
@Description({"Get the location of a scheduled event.",
"Returns the specified place if the event is external, or the audio channel's ID."})
@Since("4.8.0")
public class EventLocation extends SimpleScheduledEventExpression<Object> {

	static {
		register(
				EventLocation.class,
				Object.class,
				"scheduled [event] location",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event location";
	}

	@Override
	public @Nullable Object convert(ScheduledEvent scheduledEvent) {
		final String location = scheduledEvent.getLocation();
		if (scheduledEvent.getType().isChannel())
			return scheduledEvent.getJDA().getChannelById(AudioChannel.class, location);

		return location;
	}

	@Override
	public RestAction<?> change(ScheduledEvent entity, Object[] delta) {
		final Object location = delta[0];
		if (location instanceof final AudioChannel audioChannel) {
			return entity.getManager().setLocation(audioChannel);
		} else {
			return entity.getManager().setLocation((String) location);
		}
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
