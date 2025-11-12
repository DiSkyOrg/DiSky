package net.itsthesky.disky.elements.getters;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Get Bot / Bot Named X")
@Description({"Get a cached bot from DiSky using its unique name.",
        "If the desired bot does not exist or is not loaded yet, this expression will return none.",
        "This expression cannot be changed."})
@Examples({"get bot \"name\"",
        "bot named \"name\""})
@Since("4.0.0")
public class GetBot extends SimpleExpression<Bot> {

    static {
        Skript.registerExpression(
                GetBot.class,
                Bot.class,
                ExpressionType.COMBINED,
                "[get] [the] bot [(named|with name)] %string%"
        );
    }

    private Expression<String> exprName;

    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
        exprName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    protected Bot @NotNull [] get(@NotNull Event e) {
        final String name = exprName.getSingle(e);
        if (name == null)
            return new Bot[0];
        final @Nullable Bot bot = DiSky.getManager().fromName(name);
        if (bot == null)
            DiSkyRuntimeHandler.error(new RuntimeException("Unable to get the bot named " + name + ", its not loaded or not enabled."));
        return bot == null ? new Bot[0] : new Bot[] {bot};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Bot> getReturnType() {
        return Bot.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "bot named " + exprName.toString(e, debug);
    }
}
