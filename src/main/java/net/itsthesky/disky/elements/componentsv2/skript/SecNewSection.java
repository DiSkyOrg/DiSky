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

import net.itsthesky.disky.api.skript.ReturningSection;
import net.itsthesky.disky.elements.componentsv2.base.SectionBuilder;
import net.itsthesky.disky.elements.componentsv2.base.SectionBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SecNewSection extends ReturningSection<SectionBuilder> {

    public static class section extends LastBuilderExpression<SectionBuilder, SecNewSection> { }

    static {
        register(
                SecNewSection.class,
                SectionBuilder.class,
                section.class,
                "(make|create) [a] [new] [discord] container section"
        );
    }

    @Override
    public SectionBuilder createNewValue(@NotNull Event event) {
        return new SectionBuilder();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create new container";
    }

}
