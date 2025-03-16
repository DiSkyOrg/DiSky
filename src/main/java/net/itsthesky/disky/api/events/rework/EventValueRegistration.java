package net.itsthesky.disky.api.events.rework;

import net.dv8tion.jda.api.events.Event;

import java.util.function.Function;

/**
 * A class that holds registration information for an event value.
 *
 * @param <T> The JDA event type
 * @param <V> The value type
 */
class EventValueRegistration<T extends Event, V> {
    private final Class<V> valueClass;
    private final Function<T, V> mapper;
    private final int time;

    /**
     * Creates a new event value registration.
     *
     * @param valueClass The class of the value
     * @param mapper A function to extract the value from the JDA event
     * @param time The time (-1 for past, 0 for present, 1 for future)
     */
    EventValueRegistration(Class<V> valueClass, Function<T, V> mapper, int time) {
        this.valueClass = valueClass;
        this.mapper = mapper;
        this.time = time;
    }

    /**
     * Gets the class of the value.
     *
     * @return The value class
     */
    Class<V> getValueClass() {
        return valueClass;
    }

    /**
     * Gets the mapper function.
     *
     * @return The mapper function
     */
    Function<T, V> getMapper() {
        return mapper;
    }

    /**
     * Gets the time.
     *
     * @return The time
     */
    int getTime() {
        return time;
    }
}