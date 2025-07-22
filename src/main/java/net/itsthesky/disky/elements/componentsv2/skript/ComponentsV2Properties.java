package net.itsthesky.disky.elements.componentsv2.skript;

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

import ch.njol.skript.classes.Changer;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.api.skript.reflects.ReflectChangeablePropertyFactory;
import net.itsthesky.disky.elements.componentsv2.base.ContainerBuilder;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.FileDisplayBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.SeparatorBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.TextDisplayBuilder;

import static net.itsthesky.disky.api.skript.reflects.ReflectClassFactory.register;

public final class ComponentsV2Properties {

    private static String COMPONENTS_TYPE_UNION = "filedisplaycomponent/textdisplaycomponent/separatorcomponent";

    static {

        register(COMPONENTS_TYPE_UNION, "unique id", Number.class, "unique id",
                component -> ((INewComponentBuilder<?>) component).getUniqueId());

        ReflectChangeablePropertyFactory.registerChangeable(
                COMPONENTS_TYPE_UNION, "unique id",
                INewComponentBuilder.class,
                Number.class, "unique id",
                INewComponentBuilder::getUniqueId,
                mode -> mode == Changer.ChangeMode.SET ? new Class[]{Number.class} : null,

                (componentBuilder, changeData) -> {
                    if (changeData.getMode() == Changer.ChangeMode.SET) {
                        final var newUniqueId = changeData.getFirstDelta();
                        if (newUniqueId != null) {
                            componentBuilder.setUniqueId(newUniqueId.intValue());
                        }
                    }
                    return null;
                }
        );

        ReflectChangeablePropertyFactory.registerChangeable(
                "container/filedisplaycomponent", "spoiler [state]",
                Object.class,
                Boolean.class, "spoiler [state]",
                obj -> {
                    if (obj instanceof final ContainerBuilder containerBuilder)
                        return containerBuilder.isSpoiler();
                    if (obj instanceof final FileDisplayBuilder fileDisplayBuilder)
                        return fileDisplayBuilder.isSpoiler();

                    return false;
                },
                mode -> mode == Changer.ChangeMode.SET ? new Class[]{Boolean.class} : null,

                (componentBuilder, changeData) -> {
                    if (changeData.getMode() == Changer.ChangeMode.SET) {
                        final var newSpoilerState = changeData.getFirstDelta();
                        if (newSpoilerState != null) {
                            if (componentBuilder instanceof final ContainerBuilder containerBuilder) {
                                containerBuilder.setSpoiler(newSpoilerState);
                            } else if (componentBuilder instanceof final FileDisplayBuilder fileDisplayBuilder) {
                                fileDisplayBuilder.setSpoiler(newSpoilerState);
                            }
                        }
                    }
                    return null;
                }
        );

        // Text Display Component
        register("textdisplaycomponent",
                "text", String.class, "text",
                TextDisplayBuilder::getText);

        // Separator Component
        register("separatorcomponent",
                "spacing", String.class, "spacing",
                separator -> ((SeparatorBuilder) separator).getSpacing().name().toLowerCase());
    }

}
