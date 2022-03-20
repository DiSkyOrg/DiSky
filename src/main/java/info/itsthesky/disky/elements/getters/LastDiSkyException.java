package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Last DiSky Exception")
@Description({"Return the last occurred DiSky or Discord exception in the current event.",
"This expression is event-based, means you cannot get the last error that happened on another event.",
"Once this has been called, it will remove the returned value from the errors list to avoid having two times the same error message."})
@Examples("if last disky exception is set: # an error occurred")
public class LastDiSkyException extends SimpleExpression<String> {

	static {
		Skript.registerExpression(
				LastDiSkyException.class,
				String.class,
				ExpressionType.SIMPLE,
				"[the] last (disky|discord) (error|exception)"
		);
	}

	@Override
	protected String @NotNull [] get(@NotNull Event e) {
		final @Nullable Throwable throwable = DiSky.getErrorHandler().getErrorValue(e);
		if (throwable == null)
			return new String[0];
		return new String[] {throwable.getMessage()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "the last disky exception";
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		return true;
	}
}
