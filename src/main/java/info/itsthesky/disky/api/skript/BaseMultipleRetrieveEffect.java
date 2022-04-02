package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Mainly same as {@link BaseRetrieveEffect} but works for storing multiple entities instead.
 */
public abstract class BaseMultipleRetrieveEffect<T extends List, E> extends SpecificBotEffect<T> {

    public static void register(Class<? extends WaiterEffect<?>> clazz,
                                String entityName,
                                String sourceInfo) {
        Skript.registerEffect(
                clazz,
                "retrieve [(all|every)] " + entityName + " (from|with|of|in) %"+sourceInfo+"% [(with|using) [the] [bot] %-bot%] and store (them|the "+entityName+") in %-objects%"
        );
    }

    private Expression<E> exprEntity;
    private Expression<Bot> exprBot;

    @Override
    @SuppressWarnings("unchecked")
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprEntity = (Expression<E>) expressions[0];
        exprBot = (Expression<Bot>) expressions[1];

        return validateVariable(expressions[2], true);
    }

    protected List<?> convert(T original) {
        return original;
    };

    protected abstract RestAction<T> retrieve(@NotNull E entity);

    @Override
    public void runEffect(Event e, Bot bot) {
        E entity = exprEntity.getSingle(e);

        if (entity == null) {
            restart();
            return;
        }

        try {
            final RestAction<T> action = retrieve(entity);
            action.queue(values -> {
                final List<?> temp = convert(values);
                forceRestart(temp);
            }, ex -> {
                DiSky.getErrorHandler().exception(e, ex);
                restart();
            });
        } catch (Exception ex) {
            DiSky.getErrorHandler().exception(e, ex);
            restart();
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "retrieve all entities from entity " + exprEntity.toString(e, debug) + " with bot " + exprBot.toString(e, debug) + " and store them in " + getChangedVariable().toString(e, debug);
    }

}
