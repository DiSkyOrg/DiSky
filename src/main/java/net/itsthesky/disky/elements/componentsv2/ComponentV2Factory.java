package net.itsthesky.disky.elements.componentsv2;

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
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import net.itsthesky.disky.elements.componentsv2.base.ContainerBuilder;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import net.itsthesky.disky.elements.componentsv2.base.SectionBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.*;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Factory class to dynamically create components from JDA's components, to
 * DiSky's builder system.
 *
 * @author ItsTheSky
 */
public final class ComponentV2Factory {

    public static final Map<Class<? extends Component>, Class<? extends INewComponentBuilder<?>>> REGISTERED_COMPONENTS = Map.of(
            Container.class, ContainerBuilder.class,
            Section.class, SectionBuilder.class,

            TextDisplay.class, TextDisplayBuilder.class,
            Separator.class, SeparatorBuilder.class,
            MediaGallery.class, MediaGalleryBuilder.class,
            FileDisplay.class, FileDisplayBuilder.class,
            Button.class, ButtonBuilder.class,
            Thumbnail.class, ThumbnailBuilder.class,
            ActionRow.class, ComponentRow.class
    );

    public static <T extends Component> INewComponentBuilder<T> recreateComponent(@Nullable T component) {
        if (component == null)
            return null;

        try {
            final var builderClass = REGISTERED_COMPONENTS.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().isAssignableFrom(component.getClass()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);

            if (builderClass == null) {
                //throw new IllegalArgumentException("No builder registered for component: " + component.getClass().getName());
                DiSky.debug("No builder registered for component: " + component.getClass().getName() + ", using default builder.");
                return null;
            }

            final var builder = (INewComponentBuilder<T>) builderClass.getDeclaredConstructor().newInstance();
            builder.loadFrom(component);
            return builder;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to recreate component: " + component.getUniqueId(), e);
        }
    }
}
