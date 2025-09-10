package net.itsthesky.disky.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.api.skript.WaiterEffect;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Execute X Using Bot")
@Description({"This effect is for utilities purpose.",
"It will wrap the actual effect from DiSky and execute it using the specified bot.",
"The syntax MUST come from DiSky, and at least ONE bot MUST be loaded (if the specified one is wrong / not loaded)"})
public class BaseBotEffect extends WaiterEffect<Object> {

    static {
        Skript.registerEffect(BaseBotEffect.class,
                "execute (with|using) [the] %bot% <.+>",
                "execute <.+> (with|using) [the] %bot%");
    }

    private Expression<Bot> exprBot;
    private SpecificBotEffect<Object> effect;

    @Override
    public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        exprBot = (Expression<Bot>) expressions[0];
        final String rawEffect = parseResult.regexes.get(0).group();
        final Effect unparsedEffect = Effect.parse(rawEffect, "Can't understand this effect: " + rawEffect);
        if (!(unparsedEffect instanceof SpecificBotEffect<?>))
            return false;
        effect = (SpecificBotEffect<Object>) unparsedEffect;
        return true;
    }

    @Override
    public void runEffect(Event e) {
        final @NotNull Bot bot =
                parseSingle(exprBot, e, DiSky.getManager().findAny());
        if (bot == null) {
            DiSkyRuntimeHandler.error(new RuntimeException("No bot is currently loaded on the server. You cannot use any DiSky syntaxes without least one loaded."));
            restart();
            return;
        }
        effect.setNext(getNext());
        effect.runEffect(e, bot);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "execute " + effect.toString(e, debug) + " using bot " + exprBot.toString(e, debug);
    }
}
