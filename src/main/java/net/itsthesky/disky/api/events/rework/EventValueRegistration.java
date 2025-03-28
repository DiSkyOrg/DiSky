package net.itsthesky.disky.api.events.rework;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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