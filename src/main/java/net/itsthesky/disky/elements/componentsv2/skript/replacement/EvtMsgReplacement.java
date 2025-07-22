package net.itsthesky.disky.elements.componentsv2.skript.replacement;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.componentsv2.base.INewComponentBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Getter
public class EvtMsgReplacement extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    static {
        ReflectEventExpressionFactory.registerSingleEventExpression(
                "[the] component",
                EvtMsgReplacement.class,
                INewComponentBuilder.class,
                EvtMsgReplacement::getComponent
        );

        ReflectEventExpressionFactory.registerSingleEventExpression(
                "[the] unique id",
                EvtMsgReplacement.class,
                Number.class,
                EvtMsgReplacement::getUniqueId
        );

        SkriptUtils.registerValue(
                EvtMsgReplacement.class,
                INewComponentBuilder.class,
                EvtMsgReplacement::getReplacement
        );
    }

    private final int uniqueId;
    private final INewComponentBuilder<?> component;
    private @Nullable INewComponentBuilder<?> replacement;

    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}