package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.EffChange;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.ReflectionUtils;
import net.itsthesky.disky.elements.changers.IAsyncChangeableExpression;
import net.itsthesky.disky.elements.changers.IAsyncGettableExpression;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Await Effect")
@Description({"Forces an effect to be executed asynchronously and waits for its completion.",
        "This is useful for non-async effects that need to run in an async context.",
        "Note: If the effect is already async, using await is redundant."})
@Examples({"await set {_value} to mention tag \"test\" with id \"000\" # Forces async execution",
        "await add role with id \"000\" to event-member # Waits for role addition"})
@Since("4.0.0")
public class AwaitEffect extends AsyncEffect {

    static {
        Skript.registerEffect(AwaitEffect.class,
                "await <.+>");
    }

    private Effect effect;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        final String rawEffect = parseResult.regexes.get(0).group();
        effect = Effect.parse(rawEffect, "Can't understand this effect: " + rawEffect);
        if (effect == null)
            return false;

        if (effect instanceof AsyncEffect)
            Skript.warning("You're using an async effect inside an await effect. No need to use await in this case as the effect is already async.");

        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        // Custom handler for the change effect as it requires to run the change method
        // of expressions asynchronously
        if (effect instanceof EffChange) {
            final EffChange change = (EffChange) effect;
            try {

                final Expression<?> changed = ReflectionUtils.getFieldValue(EffChange.class, "changed", change);
                if (changed == null)
                {
                    DiSky.debug("The changed expression is null, skipping the await effect.");
                    return;
                }

                final Expression<?> changer = ReflectionUtils.getFieldValue(EffChange.class, "changer", change);
                final Changer.ChangeMode mode = ReflectionUtils.getFieldValue(EffChange.class, "mode", change);
                Object[] delta;
                if (changer instanceof IAsyncGettableExpression)
                    delta = ((IAsyncGettableExpression) changer).getArrayAsync(event, changer);
                else
                    delta = changer == null ? null : changer.getArray(event);

                if (changed instanceof IAsyncChangeableExpression) {
                    final IAsyncChangeableExpression asyncChangeable = (IAsyncChangeableExpression) changed;

                    delta = changer == null ? delta : changer.beforeChange(changed, delta);
                    if ((delta == null || delta.length == 0) && (mode != Changer.ChangeMode.DELETE && mode != Changer.ChangeMode.RESET)) {
                        if (mode == Changer.ChangeMode.SET && changed.acceptChange(Changer.ChangeMode.DELETE) != null)
                            asyncChangeable.changeAsync(event, null, Changer.ChangeMode.DELETE);
                        return;
                    }
                    asyncChangeable.changeAsync(event, delta, mode);
                } else {
                    changed.change(event, delta, mode);
                }

                return;
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error((Exception) ex);
                return;
            }
        }

        effect.run(event);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "await " + effect.toString(event, debug);
    }

}