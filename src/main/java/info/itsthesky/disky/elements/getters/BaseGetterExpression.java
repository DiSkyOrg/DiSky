package info.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unchecked"})
public abstract class BaseGetterExpression<T> extends SimpleExpression<T> implements IAsyncGettableExpression<T> {

    protected static <T> void register(Class clazz,
                                       Class type,
                                       String codeName) {
        register(clazz, type, codeName, "id", true);
    }

    protected static <T> void register(Class clazz,
                                       Class type,
                                       String codeName,
                                       String property,
                                       boolean allowBot) {
        Skript.registerExpression(clazz,
                type,
                ExpressionType.COMBINED,
                codeName + " (with|from) [the] "+property+" %string% "+ (allowBot ? "[(with|using) [the] [bot] [(named|with name)] %-bot%]" : ""));
    }

    protected Node node;
    protected Expression<String> exprId;
    protected Expression<Bot> exprBot;

    protected abstract T get(String id, Bot bot);
    protected T getAsync(String id, Bot bot) {
        return get(id, bot);
    };

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
        exprId = (Expression<String>) exprs[0];
        exprBot = (Expression<Bot>) exprs[1];
        node = getParser().getNode();
        return true;
    }

    @Override
    protected @Nullable
    T @NotNull [] get(@NotNull Event e) {
        final String id = exprId.getSingle(e);
        final Bot bot = Bot.fromContext(exprBot, e);
        if (EasyElement.anyNull(this, id, bot))
            return (T[]) new Object[0];

        if (!SkriptUtils.validateSnowflake(id, node))
            return (T[]) new Object[0];

        return (T[]) new Object[] {get(id, bot)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    protected boolean allowBot() {
        return true;
    }

    public abstract String getCodeName();

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return getCodeName() + " with id " + exprId.toString(e, debug) + (
                exprBot != null ? "using the bot " + exprBot.toString(e, debug) : ""
                );
    }

    @Override
    public T[] getAsync(Event e) {
        final String id = exprId.getSingle(e);
        final Bot bot = Bot.fromContext(exprBot, e);
        if (EasyElement.anyNull(this, id, bot))
            return (T[]) new Object[0];

        if (!SkriptUtils.validateSnowflake(id, node))
            return (T[]) new Object[0];

        return (T[]) new Object[] {getAsync(id, bot)};
    }
}
