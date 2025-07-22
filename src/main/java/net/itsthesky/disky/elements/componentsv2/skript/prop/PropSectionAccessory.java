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
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.componentsv2.ComponentV2Factory;
import net.itsthesky.disky.elements.componentsv2.base.ISectionAccessoryBuilder;
import net.itsthesky.disky.elements.componentsv2.base.SectionBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.ButtonBuilder;
import net.itsthesky.disky.elements.componentsv2.base.sub.ThumbnailBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class PropSectionAccessory extends SimplePropertyExpression<SectionBuilder, ISectionAccessoryBuilder> {

    static {
        register(PropSectionAccessory.class,
                ISectionAccessoryBuilder.class,
                "accessory [component]",
                "containersection");
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.DELETE)
            return new Class[]{
                    String.class,
                    ThumbnailBuilder.class,
                    ButtonBuilder.class
            };
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        final SectionBuilder section = EasyElement.parseSingle(getExpr(), event);
        if (section == null)
            return;

        switch (mode) {
            case SET -> {
                assert delta != null;

                for (Object comp : delta) {
                    if (comp instanceof String source) {
                        Thumbnail thumbnail;
                        if (source.startsWith("http"))
                            thumbnail = Thumbnail.fromUrl(source);
                        else
                            thumbnail = Thumbnail.fromFile(JDAUtils.parseFile(source));
                        final var builder = (ISectionAccessoryBuilder<?>) ComponentV2Factory.recreateComponent(thumbnail);
                        section.setAccessoryComponent(builder);
                    } else if (comp instanceof ISectionAccessoryBuilder<?> accessory) {
                        section.setAccessoryComponent(accessory);
                    }
                }
            }
            case DELETE -> section.setAccessoryComponent(null);
        }
    }

    @Override
    public @Nullable ISectionAccessoryBuilder convert(SectionBuilder from) {
        return from.getAccessoryComponent();
    }

    @Override
    protected String getPropertyName() {
        return "accessory component";
    }

    @Override
    public Class<? extends ISectionAccessoryBuilder> getReturnType() {
        return ISectionAccessoryBuilder.class;
    }

}
