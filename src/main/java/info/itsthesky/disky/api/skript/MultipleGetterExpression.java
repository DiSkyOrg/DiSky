package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.events.GenericEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MultipleGetterExpression<T, E extends Event> extends SimpleExpression<T> {

	protected abstract String getValue();

	protected abstract Class<? extends Event> getEvent();

	protected abstract T[] gets(E event);

	@Override
	@SuppressWarnings("unchecked")
	protected T @NotNull [] get(@NotNull Event e) {
		return gets((E) e);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return getValue();
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		if (!EasyElement.containsEvent(getEvent())) {
			Skript.error(getValue() + " cannot be used in a " + ParserInstance.get().getCurrentEventName());
			return false;
		}
		return true;
	}
}
