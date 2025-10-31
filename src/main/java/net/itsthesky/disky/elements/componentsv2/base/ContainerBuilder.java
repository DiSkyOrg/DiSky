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

import ch.njol.skript.util.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.container.Container;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.core.Utils;
import net.itsthesky.disky.elements.componentsv2.ComponentV2Factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ContainerBuilder implements INewComponentBuilder<Container> {

    private final List<IContainerComponentBuilder<?>> components = new ArrayList<>();
    private int uniqueId = -1;
    private boolean spoiler = false;
    private Color accentColor = null;

    public ContainerBuilder(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void addContent(IContainerComponentBuilder<?> component) {
        if (component == null) {
            throw new IllegalArgumentException("Component cannot be null");
        }
        components.add(component);
    }

    @Override
    public void loadFrom(Container component) {
        if (component == null)
            throw new IllegalArgumentException("Component cannot be null");

        components.clear();
        for (var child : component.getComponents()) {
            INewComponentBuilder<?> newComp = ComponentV2Factory.recreateComponent(child);
            if (newComp instanceof IContainerComponentBuilder<?> builder)
                this.components.add(builder);
        }

        this.uniqueId = component.getUniqueId();
        this.accentColor = SkriptUtils.convert(component.getAccentColor());
        this.spoiler = component.isSpoiler();
    }

    @Override
    public Container build() {
        var base = Container.of(components.stream()
                .map(IContainerComponentBuilder::buildWithId)
                .toList());

        if (accentColor != null)
            base = base.withAccentColor(SkriptUtils.convert(accentColor));

        return base.withSpoiler(spoiler);
    }
}
