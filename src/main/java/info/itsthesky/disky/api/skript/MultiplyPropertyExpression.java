package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class MultiplyPropertyExpression<F, T> extends SimpleExpression<T> {

    public Expression<? extends F> expr;

    protected static <T> void register(final Class<? extends Expression<T>> c, final Class<T> type, final String property, final String fromType) {
        Skript.registerExpression(c, type, ExpressionType.SIMPLE,
                "[all] [the] " + property + " of %" + fromType + "%",
                "[all] [the] %" + fromType + "%'[s] " + property
        );
    }

    public abstract @NotNull Class<? extends T> getReturnType();

    protected abstract String getPropertyName();

    protected abstract T[] convert(F t);

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(final Expression<?> @NotNull [] expr, final int matchedPattern, final @NotNull Kleenean isDelayed, final SkriptParser.@NotNull ParseResult parseResult) {
        this.expr = (Expression<? extends F>) expr[0];
        return true;
    }

    @Override
    protected T @NotNull [] get(@NotNull Event e) {
        if (expr.getSingle(e) == null)
            return (T[]) new Object[0];
        return convert(expr.getSingle(e));
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        return "the " + getPropertyName() + " of " + expr.toString(e, debug);
    }

    public Expression<? extends F> getExpr() {
        return expr;
    }
}