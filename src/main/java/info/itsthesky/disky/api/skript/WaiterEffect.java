package info.itsthesky.disky.api.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Effect that can easily manage how the TriggerItem are executed.
 * @author ItsTheSky
 */
@SuppressWarnings({"unchecked", "unused", "raw"})
public abstract class WaiterEffect<T> extends EasyElement {

    protected Event event;
    private Object localVars;
    private NodeInformation node;
    @Nullable protected Variable<T> changedVariable = null;
    private boolean isStopped;

    public abstract boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult);

    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, @NotNull SkriptParser.ParseResult parseResult) {
        ParserInstance.get().setHasDelayBefore(Kleenean.TRUE);
        node = new NodeInformation();
        return initEffect(expressions, i, kleenean, parseResult);
    }

    public boolean isStopped() {
        return isStopped;
    }

    public NodeInformation getNode() {
        return node;
    }

    public abstract void runEffect(Event e);

    protected boolean validateVariable(final Expression<?> expr, boolean shouldBeList) {
        return validateVariable(expr, shouldBeList, false);
    }

    protected String variableAsString(Event e, boolean debug) {
        return getChangedVariable() == null ? "none" : getChangedVariable().toString(e, debug);
    }

    protected boolean validateVariable(final Expression<?> expr, boolean shouldBeList, boolean allowNotSet) {
        if (expr == null && allowNotSet)
            return true;
        final boolean validVariable = validate(expr, shouldBeList);
        if (validVariable)
            setChangedVariable((Variable<T>) expr);
        return validVariable;
    }

    public void setChangedVariable(@Nullable Variable<T> changedVariable) {
        this.changedVariable = changedVariable;
    }

    public @Nullable Variable<T> getChangedVariable() {
        return changedVariable;
    }

    public void clearChangedVariable() {
        setChangedVariable(null);
    }

    /*
    Working on walk method so :>
     */
    @Override
    protected void execute(@NotNull Event event) { }

    @Nullable
    @Override
    protected TriggerItem walk(@NotNull Event e) {
        isStopped = true;
        debug(e, true);
        event = e;

        localVars = Variables.removeLocals(e);
        if (localVars != null)
            Variables.setLocalVariables(e, localVars);

        if (!Skript.getInstance().isEnabled()) // See https://github.com/SkriptLang/Skript/issues/3702
        {
            isStopped = false;
            return null;
        }

        try {
            runEffect(e);
        } catch (Throwable ex) {
            DiSky.getErrorHandler().exception(event, ex);
        }

        return null;
    }

    protected void restart() {
        runItems();
    }

    protected void forceRestart(Object object) {
        forceRunItems(object);
    }

    protected void restart(T object) {
        runItems(object);
    }

    protected void changeVariable(Event e, Object object) {
        if (object instanceof List)
            object = ((List) object).toArray(new Object[0]);
        if (changedVariable != null)
            changedVariable.change(e, (object instanceof Object[] ? (Object[]) object : new Object[]{object}), Changer.ChangeMode.SET);
    }

    protected void runItems(Event e, Object object) {

        isStopped = false;

        if (localVars != null)
            Variables.setLocalVariables(e, localVars);

        changeVariable(e, object);

        localVars = Variables.removeLocals(e);

        if (getNext() != null) {
            Bukkit.getScheduler().runTask(Skript.getInstance(), () -> { // Walk to next item synchronously
                Object timing = null;
                if (SkriptTimings.enabled()) { // getTrigger call is not free, do it only if we must
                    Trigger trigger = getTrigger();
                    if (trigger != null)
                        timing = SkriptTimings.start(trigger.getDebugLabel());
                }

                if (localVars != null)
                    Variables.setLocalVariables(e, localVars);
                TriggerItem.walk(getNext(), e);
                SkriptTimings.stop(timing); // Stop timing if it was even started
            });
        }

    }

    protected void runItems() {
        runItems(this.event, null);
    }

    protected void runItems(T object) {
        runItems(this.event, object);
    }

    protected void forceRunItems(Object object) {
        runItems(this.event, object);
    }
}