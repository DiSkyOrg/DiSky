package net.itsthesky.disky.api.skript;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleGetterExpression<T, E extends Event> extends SimpleExpression<T> {

	protected abstract String getValue();

	protected abstract Class<E> getEvent();

	protected abstract T convert(E event);

	@Override
	@SuppressWarnings("unchecked")
	protected T @NotNull [] get(@NotNull Event e) {
		return (T[]) new Object[] {convert((E) e)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return getValue();
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		return getEvent() == null || EasyElement.containsEvent(getEvent());
	}
}
