package info.itsthesky.disky.elements.properties.events;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Cover of Scheduled Event")
@Description({"Get the cover of a scheduled event.",
"Links to a potentially heavily compressed image. You can append a size parameter to the URL if needed. Example: ?size=4096",
"This can returns null if no cover is set for the event."})
@Since("4.8.0")
public class EventCover extends SimpleScheduledEventExpression<String> {

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
	public RestAction<?> change(ScheduledEvent entity, Object[] delta) {
		final String cover = delta[0] == null ? null : (String) delta[0];
		final Icon icon = cover == null ? null : SkriptUtils.parseIcon(cover);

		return entity.getManager().setImage(icon);
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

}
