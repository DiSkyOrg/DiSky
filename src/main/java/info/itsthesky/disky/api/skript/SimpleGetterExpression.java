package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.events.GenericEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class SimpleGetterExpression<T, E extends GenericEvent> extends SimpleExpression<T> {

	private final Class<T> clazz;
	private final String value;
	private final Class<? extends Event> event;

	public SimpleGetterExpression(Class<T> clazz,
								  String value,
								  Class<? extends Event> event) {
		this.clazz = clazz;
		this.value = value;
		this.event = event;
	}

	protected abstract T get(E event);

	@Override
	@SuppressWarnings("unchecked")
	protected T @NotNull [] get(@NotNull Event e) {
		return (T[]) new Object[] {get((E) e)};
	}

	@Override
	public @NotNull Class<? extends T> getReturnType() {
		return clazz;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return value;
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		if (!EasyElement.containsEvent(event)) {
			Skript.error(value + " cannot be used in a " + ParserInstance.get().getCurrentEventName());
			return false;
		}
		return true;
	}
}
