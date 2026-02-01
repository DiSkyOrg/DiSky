package net.itsthesky.disky.elements.sections.once;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EffOuterExecution extends Effect {

    static {
        Skript.registerEffect(
                EffOuterExecution.class,
                "outer <.+>"
        );
    }

    private SecListenOnce secListenOnce;
    private Effect effect;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        secListenOnce = getParser().getCurrentSection(SecListenOnce.class);
        if (secListenOnce == null) {
            Skript.error("The 'outer' effect can only be used in a 'listen once' section.");
            return false;
        }

        final String rawEffect = parseResult.regexes.get(0).group();
        return secListenOnce.executeInOuter(() -> {
            effect = Effect.parse(rawEffect, "Can't understand this effect: " + rawEffect);
            return true;
        });
    }

    @Override
    protected void execute(@NotNull Event event) {
        effect.run(secListenOnce.getOuterEvent());
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "outer " + effect.toString(event, debug);
    }
}
