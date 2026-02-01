package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Shutdown Bot")
@Description({"Stop and disconnect a loaded bot from DiSky & discord.",
        "If any requests was still remaining, they will be executed before the actual bot shutdown",
        "Using the force pattern will cancel all requests and shutdown the bot instantly."})
@Examples({"shutdown bot named \"name\"",
        "stop bot \"name\""})
@Since("4.0.0")
public class StopBot extends AsyncEffect {

    static {
        Skript.registerEffect(
                StopBot.class,
                "[force] (stop|shutdown) [the] [bot] %bot%"
        );
    }

    private Expression<Bot> exprBot;

    boolean force;

    @Override
    public boolean init(Expression[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprBot = (Expression<Bot>) expressions[0];
        force = parseResult.expr.contains("force");
        return true;
    }

    @Override
    public void execute(Event e) {
        final Bot bot = parseSingle(exprBot, e, null);
        if (!DiSkyRuntimeHandler.checkSet(getNode(), exprBot, bot))
            return;

        bot.shutdown(force);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return (force ? "force " : "") + "shutdown bot " + exprBot.toString(e, debug);
    }
}
