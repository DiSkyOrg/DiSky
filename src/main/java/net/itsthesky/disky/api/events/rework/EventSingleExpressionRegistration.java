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

class EventSingleExpressionRegistration<T extends Event, E> {

    private final String pattern;
    private final Class<E> expressionClass;
    private final Function<T, E> expressionMapper;

    /**
     * Creates a new event expression registration.
     *
     * @param pattern The pattern used in the expression
     * @param expressionClass The class of the expression
     * @param expressionMapper A function to map the event to the expression
     */
    EventSingleExpressionRegistration(String pattern, Class<E> expressionClass, Function<T, E> expressionMapper) {
        this.pattern = pattern;
        this.expressionClass = expressionClass;
        this.expressionMapper = expressionMapper;
    }

    /**
     * Gets the pattern.
     *
     * @return The pattern
     */
    String getPattern() {
        return pattern;
    }

    /**
     * Gets the expression class.
     *
     * @return The expression class
     */
    Class<E> getExpressionClass() {
        return expressionClass;
    }

    /**
     * Gets the expression mapper function.
     *
     * @return The expression mapper function
     */
    Function<T, E> getExpressionMapper() {
        return expressionMapper;
    }
}