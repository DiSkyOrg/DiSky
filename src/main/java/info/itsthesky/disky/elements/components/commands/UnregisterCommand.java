package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnregisterCommand extends SpecificBotEffect {

    static {
        Skript.registerEffect(UnregisterCommand.class,
                "unregister [the] [command[s]] %strings% [(1¦globally|2¦locally)] (in|from|of) [the] [(bot|guild)] %bot/guild%");
    }

    private Expression<String> exprIds;
    private boolean isGlobal;
    private Expression<Object> exprEntity;

    @Override
    public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        exprIds = (Expression<String>) expressions[0];
        exprEntity = (Expression<Object>) expressions[1];
        isGlobal = (parseResult.mark & 1) != 0;
        return true;
    }


    @Override
    public void runEffect(@NotNull Event e, Bot bot) {
        final String[] ids = parseList(exprIds, e, new String[0]);
        final Object entity = parseSingle(exprEntity, e, null);
        if (ids.length == 0 || entity == null) {
            restart();
            return;
        }

        final @Nullable Guild guild = isGlobal ? null : (Guild) entity;
        final RestAction<?> action;

        if (isGlobal)
            action = RestAction.allOf(Stream.of(ids)
                    .map(id -> bot.getInstance().deleteCommandById(id))
                    .collect(Collectors.toList()));
        else
            action = RestAction.allOf(Stream.of(ids)
                    .map(guild::deleteCommandById)
                    .collect(Collectors.toList()));

        action.queue(this::restart, ex -> {
            restart();
            DiSky.getErrorHandler().exception(e, ex);
        });
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "unregister commands " + exprIds.toString(e, debug) + " in " + (isGlobal ? "global" : "local") + " in " + exprEntity.toString(e, debug);
    }
}
