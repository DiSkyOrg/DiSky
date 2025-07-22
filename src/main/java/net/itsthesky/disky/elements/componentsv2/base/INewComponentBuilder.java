package net.itsthesky.disky.elements.componentsv2.base;

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

import net.dv8tion.jda.api.components.Component;
import net.itsthesky.disky.elements.componentsv2.ComponentV2Factory;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for a new component builder, which allows to create a
 * new component from scratch or from an existing JDA component.
 * @param <C> the type of component this builder creates.
 */
public interface INewComponentBuilder<C extends Component> {

    /**
     * Loads that builder from the given JDA component.
     * @param component the JDA component to load from.
     */
    void loadFrom(C component);

    /**
     * Builds the component from the current state of the builder.
     * @return the built component.
     */
    C build();

    /**
     * Returns the unique ID of the component. (which is <b>NOT</b> the component's ID!)
     * @return the unique ID of the component, or -1 if not set.
     * @see Component#getUniqueId()
     */
    int getUniqueId();

    void setUniqueId(int uniqueId);

    /**
     * Get the Custom ID of this component. It is not the same
     * as the component's ID, but rather a unique identifier
     * for the component's action.
     * @return the custom ID of the component, or null if not set.
     */
    default @Nullable String getCustomId() {
        return null;
    }

    default C buildWithId() {
        var component = build();

        if (getUniqueId() != -1)
            component = (C) component.withUniqueId(getUniqueId());

        return component;
    }

    static <C extends Component> INewComponentBuilder<C> of(C component) {
        return ComponentV2Factory.recreateComponent(component);
    }
}
