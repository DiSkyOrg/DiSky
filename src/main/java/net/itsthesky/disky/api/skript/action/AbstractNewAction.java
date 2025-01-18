package net.itsthesky.disky.api.skript.action;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.managers.BotChangers;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNewAction<T, E> extends SimpleExpression<T> {

    private Expression<E> exprGuild;
    private Expression<Bot> exprBot;

    protected abstract T create(@NotNull E guild);

    @Override
    protected T @NotNull [] get(@NotNull Event e) {
        final E guild = EasyElement.parseSingle(exprGuild, e, null);
        final @Nullable Bot bot = EasyElement.parseSingle(exprBot, e, null);
        if (EasyElement.anyNull(this, guild))
            return (T[]) new Object[0];
        final E parsedEntity = bot == null ? guild : BotChangers.convert(guild, bot);
        if (EasyElement.anyNull(this, parsedEntity))
            return (T[]) new Object[0];
        return (T[]) new Object[] {create(parsedEntity)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    public abstract String getNewType();

    public abstract String entityToString(Expression<E> entity, Event e, boolean debug);

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "a new "+ getNewType() +" action " + entityToString(exprGuild, e,  debug);
    }

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprGuild = (Expression<E>) exprs[0];
        exprBot = (Expression<Bot>) exprs[1];
        return true;
    }

}
