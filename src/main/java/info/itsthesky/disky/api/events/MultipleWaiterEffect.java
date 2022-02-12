package info.itsthesky.disky.api.events;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.WaiterEffect;
import org.bukkit.event.Event;

import java.util.Arrays;
import java.util.List;

/**
 * Same as {@link WaiterEffect} but allow a list of variable as the generic type
 * @author Sky
 */
public abstract class MultipleWaiterEffect<T> extends WaiterEffect<T> {

    @Override
    @SuppressWarnings("unchecked")
    protected void changeVariable(Event e, Object objects) {
        if (changedVariable != null)
            changedVariable.change(e, ((List<T>) objects).toArray(new Object[0]), Changer.ChangeMode.SET);
    }

    @Override
    @Deprecated
    protected void restart(T object) {
        super.restart(object);
    }

    @SuppressWarnings("unchecked")
    protected void restart(T[] objects) {
        super.restart((T) Arrays.asList(objects));
    }
}
