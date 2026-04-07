package net.itsthesky.disky.api.skript;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

public abstract class SimpleGetterExpression<T, E extends Event> extends SimpleExpression<T> {

	protected abstract String getValue();

	protected abstract Class<E> getEvent();

	protected abstract T convert(E event);

	public Class<E>[] getCompatibleEvents() {
		Class<E>[] events = new Class[1];
		events[0] = getEvent();
		return events;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T @NotNull [] get(@NotNull Event e) {
		T[] array = (T[]) Array.newInstance(getReturnType(), 1);
		array[0] = convert((E) e);
		return array;
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
		return Stream.of(getCompatibleEvents())
				.anyMatch(EasyElement::containsEvent);
	}
}
