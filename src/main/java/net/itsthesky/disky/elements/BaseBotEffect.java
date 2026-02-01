package net.itsthesky.disky.elements;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.SpecificBotEffect;
import net.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Execute X Using Bot")
@Description({"This effect is for utilities purpose.",
        "It will wrap the actual effect from DiSky and execute it using the specified bot.",
        "The syntax MUST come from DiSky, and at least ONE bot MUST be loaded (if the specified one is wrong / not loaded)"})
public class BaseBotEffect extends AsyncEffect {

    static {
        Skript.registerEffect(BaseBotEffect.class,
                "execute (with|using) [the] %bot% <.+>",
                "execute <.+> (with|using) [the] %bot%");
    }

    private Expression<Bot> exprBot;
    private SpecificBotEffect<Object> effect;

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
        Skript.error("The 'execute with' effect has been removed; you now have to specify your bot when GETTING/RETRIEVING entities!");
        return false;
    }

    @Override
    public void execute(Event e) {
        throw new UnsupportedOperationException("Use executeEffect with DiSkyRuntimeHandler");
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "execute " + effect.toString(e, debug) + " using bot " + exprBot.toString(e, debug);
    }
}
