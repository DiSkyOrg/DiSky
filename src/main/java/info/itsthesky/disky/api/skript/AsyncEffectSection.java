package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.EffectSection;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an effect section that runs asynchronously.
 * Made by Sky, inspired by Skript's {@link ch.njol.skript.util.AsyncEffect} & {@link ch.njol.skript.lang.EffectSection}
 */
public abstract class AsyncEffectSection extends EffectSection {

    @Override
    @Nullable
    protected TriggerItem walk(@NotNull Event e) {
        debug(e, true);

        Delay.addDelayedEvent(e); // Mark this event as delayed
        Object localVars = Variables.removeLocals(e); // Back up local variables

        if (!Skript.getInstance().isEnabled()) // See https://github.com/SkriptLang/Skript/issues/3702
            return null;

        Bukkit.getScheduler().runTaskAsynchronously(Skript.getInstance(), () -> {
            // Re-set local variables
            if (localVars != null)
                Variables.setLocalVariables(e, localVars);

            execute(e); // Execute this effect

            if (getNext() != null) {
                Bukkit.getScheduler().runTask(Skript.getInstance(), () -> { // Walk to next item synchronously
                    Object timing = null;
                    if (SkriptTimings.enabled()) { // getTrigger call is not free, do it only if we must
                        Trigger trigger = getTrigger();
                        if (trigger != null) {
                            timing = SkriptTimings.start(trigger.getDebugLabel());
                        }
                    }

                    TriggerItem.walk(getNext(), e);

                    Variables.removeLocals(e); // Clean up local vars, we may be exiting now

                    SkriptTimings.stop(timing); // Stop timing if it was even started
                });
            } else {
                Variables.removeLocals(e);
            }
        });
        return null;
    }

    protected abstract void execute(Event e);
}
