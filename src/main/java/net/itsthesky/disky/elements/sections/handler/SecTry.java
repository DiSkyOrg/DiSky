package net.itsthesky.disky.elements.sections.handler;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.core.SkriptUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SecTry extends Section {

    static {
        Skript.registerSection(
                SecTry.class,
                "try",
                "catch [the] [exception] %~objects% [(stop:[and] stop)]"
        );
    }

    public enum TryType {
        TRY,
        CATCH
    }

    private TryType type;
    private @Nullable Expression<?> exceptionVariable;
    private boolean stop;

    private SecTry catchSection;
    private Trigger trigger;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions,
                        int matchedPattern,
                        @NotNull Kleenean isDelayed,
                        SkriptParser.@NotNull ParseResult parseResult,
                        @NotNull SectionNode sectionNode,
                        @NotNull List<TriggerItem> triggerItems) {
        type = matchedPattern == 0 ? TryType.TRY : TryType.CATCH;
        if (type == TryType.CATCH)
        {
            exceptionVariable = expressions[0];
            final TriggerItem last = triggerItems.get(triggerItems.size() - 1);
            if (last instanceof SecTry && ((SecTry) last).type == TryType.TRY)
            {
                ((SecTry) last).catchSection = this;
                ((SecTry) last).stop = parseResult.hasTag("stop");
                trigger = loadSectionCode(sectionNode);
                return Changer.ChangerUtils.acceptsChange(exceptionVariable, Changer.ChangeMode.SET, Exception.class);
            }

            Skript.error("Cannot have a catch section without a try section above it.");
            return false;
        }

        trigger = loadSectionCode(sectionNode);
        return true;
    }

    private Trigger loadSectionCode(SectionNode sectionNode) {
        final List<TriggerItem> items = SkriptUtils.loadCode(sectionNode);
        return new Trigger(getParser().getCurrentScript(), getParser().getCurrentEventName(),
                getParser().getCurrentSkriptEvent(), items);
    }

    @Override
    protected @Nullable TriggerItem walk(@NotNull Event event) {
        if (type == TryType.CATCH)
            return null;

        setLast(trigger, new RunnableItem(() -> {
            final List<Exception> errors = DiSkyRuntimeHandler.end();
            if (!errors.isEmpty())
            {
                if (catchSection != null)
                {
                    // TODO: Make handles for multiple errors
                    catchSection.exceptionVariable.change(event, new Exception[] {errors.get(0)}, Changer.ChangeMode.SET);
                    if (!stop)
                        setLast(catchSection.trigger, getNext().getNext());

                    TriggerItem.walk(catchSection.trigger, event);
                    return;
                }

                Skript.error("An exception occurred in a try section, but there was no catch section to handle it.");
            } else {
                TriggerItem toExecute = getNext();
                if (toExecute instanceof SecTry) // it's the catch section
                    toExecute = toExecute.getNext();

                if (toExecute != null)
                    TriggerItem.walk(toExecute, event);
            }
        }));

        DiSkyRuntimeHandler.start();
        TriggerItem.walk(trigger, event);

        return null;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return type == TryType.TRY ? "try" : "catch " + exceptionVariable.toString(event, debug);
    }

    public static class RunnableItem extends TriggerItem {

        private final Runnable runnable;
        public RunnableItem(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        protected boolean run(@NotNull Event event) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected @Nullable TriggerItem walk(Event event) {
            runnable.run();
            return getNext();
        }

        @Override
        public @NotNull String toString(@Nullable Event event, boolean debug) {
            return "runnable";
        }
    }

    public static void setLast(TriggerItem item, TriggerItem lastToChange) {
        TriggerItem next = item;
        TriggerItem last = next;
        while (next != null)
        {
            last = next;
            next = next.getNext();
        }
        if (last != null)
            last.setNext(lastToChange);
    }
}
