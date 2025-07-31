package net.itsthesky.disky.elements.componentsv2.skript.prop;

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
import ch.njol.skript.util.Color;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.itsthesky.disky.elements.componentsv2.base.ContainerBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class PropContainerAccentColor extends MultiplyPropertyExpression<ContainerBuilder, Color> {

    static {
        register(
                PropContainerAccentColor.class,
                Color.class,
                "accent color",
                "container"
        );
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (EasyElement.equalAny(mode, Changer.ChangeMode.SET))
            return new Class[]{Color.class};
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        final ContainerBuilder container = EasyElement.parseSingle(getExpr(), e);
        if (container == null) return;

        for (Object comp : delta) {
            if (comp instanceof final Color color) {
                container.setAccentColor(color);
            }
        }
    }

    @Override
    public @NotNull Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected String getPropertyName() {
        return "container accent color";
    }

    @Override
    protected Color[] convert(ContainerBuilder containerBuilder) {
        return new Color[]{containerBuilder.getAccentColor()};
    }
}
