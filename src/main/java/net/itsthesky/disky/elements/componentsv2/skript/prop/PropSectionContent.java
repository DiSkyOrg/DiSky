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
import net.dv8tion.jda.api.components.Component;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.itsthesky.disky.elements.componentsv2.base.ISectionComponentBuilder;
import net.itsthesky.disky.elements.componentsv2.base.SectionBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.TextDisplayBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class PropSectionContent extends MultiplyPropertyExpression<SectionBuilder, Component> {

    static {
        register(
                PropSectionContent.class,
                Component.class,
                "content",
                "containersection"
        );
    }

    @Override
    public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
        if (EasyElement.equalAny(mode, Changer.ChangeMode.ADD))
            return new Class[]{
                    String.class, TextDisplayBuilder.class, //for now, only texts is supported *inside* sections
            };
        return new Class[0];
    }

    @Override
    public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        final SectionBuilder section = EasyElement.parseSingle(getExpr(), e);
        if (section == null) return;

        for (Object comp : delta) {
            if (comp instanceof final String text)
                section.addContent(new TextDisplayBuilder(text, -1));
            else if (comp instanceof final ISectionComponentBuilder<?> builder)
                section.addContent(builder);
        }
    }

    @Override
    public @NotNull Class<? extends Component> getReturnType() {
        return Component.class;
    }

    @Override
    protected String getPropertyName() {
        return "section content";
    }

    @Override
    protected Component[] convert(SectionBuilder SectionBuilder) {
        return SectionBuilder.getComponents().stream()
                .map(ISectionComponentBuilder::buildWithId)
                .toArray(Component[]::new);
    }
}
