package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Channel of Scheduled Event")
@Description({"Get the channel of a scheduled event.",
"Can be null if the event is external. Will returns either a stage or voice channel."})
@Since("4.8.0")
public class EventChannel extends SimplePropertyExpression<ScheduledEvent, AudioChannel> {

	static {
		register(
				EventChannel.class,
				AudioChannel.class,
				"scheduled [event] channel",
				"scheduledevent"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "scheduled event channel";
	}

	@Override
	public @Nullable AudioChannel convert(ScheduledEvent scheduledEvent) {
		return scheduledEvent.getChannel() == null ? null : scheduledEvent.getChannel().asAudioChannel();
	}

	@Override
	public @NotNull Class<? extends AudioChannel> getReturnType() {
		return AudioChannel.class;
	}

}
