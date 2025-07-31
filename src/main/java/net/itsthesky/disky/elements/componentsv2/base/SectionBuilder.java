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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.Component;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.section.SectionAccessoryComponent;
import net.itsthesky.disky.elements.componentsv2.ComponentBuildException;
import net.itsthesky.disky.elements.componentsv2.ComponentV2Factory;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SectionBuilder implements IContainerComponentBuilder<Section> {

    private final List<ISectionComponentBuilder<?>> components = new ArrayList<>();
    private ISectionAccessoryBuilder<?> accessoryComponent;
    private int uniqueId = -1;

    public void addContent(ISectionComponentBuilder<?> component) {
        components.add(component);
    }

    @Override
    public Section build() {
        if (accessoryComponent == null)
            throw new ComponentBuildException("Section must have an accessory component set before building.");
        if (components.isEmpty() || components.size() > 3)
            throw new ComponentBuildException("Section must have between 1 and 3 content components.");

        return Section.of(accessoryComponent.buildWithId(), components.stream()
                .map(ISectionComponentBuilder::buildWithId)
                .toList());
    }

    @Override
    public void loadFrom(Section component) {
        this.accessoryComponent = (ISectionAccessoryBuilder<?>) ComponentV2Factory.recreateComponent(component.getAccessory());

        this.components.clear();
        this.components.addAll(component.getContentComponents().stream()
                .map(comp -> (ISectionComponentBuilder<?>) ComponentV2Factory.recreateComponent(comp))
                .toList());
    }

}
