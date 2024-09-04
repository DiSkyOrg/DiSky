package info.itsthesky.disky.elements.sections.once;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprOuterExpression extends SimpleExpression<Object> {

    // static {
    //     Skript.registerExpression(
    //             ExprOuterExpression.class,
    //             Object.class,
    //             ExpressionType.COMBINED,
    //             "[the] outer <.+>"
    //     );
    // }

    private Expression<?> expr;
    private SecListenOnce secListenOnce;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        secListenOnce = getParser().getCurrentSection(SecListenOnce.class);
        if (secListenOnce == null) {
            Skript.error("The 'outer' expression can only be used in a 'listen once' section.");
            return false;
        }

        var rawExpression = parseResult.regexes.get(0).group();
        expr = new SkriptParser(rawExpression, SkriptParser.PARSE_LITERALS, ParseContext.DEFAULT).parseExpression(Object.class);
        if (expr == null) {
            Skript.error("Cannot parse the given expression: " + rawExpression);
            return false;
        }

        return secListenOnce.executeInOuter(() -> expr.init(exprs, matchedPattern, isDelayed, parseResult));
    }

    @Override
    protected Object @NotNull [] get(@NotNull Event event) {
        return expr.getArray(secListenOnce.getOuterEvent());
    }

    @Override
    public boolean isSingle() {
        return expr.isSingle();
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return expr.getReturnType();
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "the outer " + expr.toString(event, debug);
    }
}
