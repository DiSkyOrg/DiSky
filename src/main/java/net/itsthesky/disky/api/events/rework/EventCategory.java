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

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Use this annotation on the class holding the registration for events
 * that are using {@link EventRegistryFactory}. The factory will gather
 * this annotation when {@link EventBuilder#createDocumentation() creating documentation} to better
 * organize the events.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EventCategory {

    /**
     * The name of the category.
     * @return The name of the category.
     */
    @NotNull String name();

    /**
     * The description of the category.
     * @return The description of the category.
     */
    @NotNull String[] description();

}
