package info.itsthesky.disky.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.elements.events.MessageEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CondMessageOrigin extends Condition {

	static {
		Skript.registerCondition(
				CondMessageOrigin.class,
				"[the] (message|event) (is coming|come from|is from) guild [channel]",
				"[the] (message|event) (is coming|come from|is from) (dm|(private|direct) message) [channel]"
		);
	}

	private boolean checkFromGuild;

	@Override
	public boolean check(@NotNull Event e) {
		final MessageEvent.BukkitMessageEvent event = (MessageEvent.BukkitMessageEvent) e;
		return checkFromGuild == event.getJDAEvent().isFromGuild();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "the message come from " + (checkFromGuild ? "guild" : "private message");
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		checkFromGuild = matchedPattern == 0;
		if (EasyElement.containsEvent(MessageEvent.BukkitMessageEvent.class))
			return true;
		Skript.error("The 'message origin' condition can only be used in a message receive event.");
		return false;
	}
}
