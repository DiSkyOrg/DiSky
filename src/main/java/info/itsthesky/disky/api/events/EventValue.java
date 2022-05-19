package info.itsthesky.disky.api.events;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Class which handle a custom event value, using an object and a class info reference.
 * @param <T> The object parameter type
 */
public class EventValue<E extends Event, T> {

    private final Function<E, T[]> getter;
    private final Class<T> aClass;
    private final String name;

    public EventValue(Class<T> aClass, String name, Function<E, T[]> getter) {
        this.getter = getter;
        this.aClass = aClass;
        this.name = name;
    }

    public Class<T> getC() {
        return aClass;
    }

    public String getName() {
        return name;
    }

    public T[] getObject(@NotNull Event e) {
        return getter.apply((E) e);
    }

}
