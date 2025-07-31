package net.itsthesky.disky.elements.componentsv2.base.sub;

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
import net.dv8tion.jda.api.components.separator.Separator;
import net.itsthesky.disky.elements.componentsv2.base.IContainerComponentBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeparatorBuilder implements IContainerComponentBuilder<Separator> {

    private boolean isInvisible;
    private Separator.Spacing spacing;
    private int uniqueId = -1;

    @Override
    public Separator build() {
        return Separator.create(!isInvisible, spacing);
    }

    @Override
    public void loadFrom(Separator component) {
        this.isInvisible = !component.isDivider();
        this.spacing = component.getSpacing();
        this.uniqueId = component.getUniqueId();
    }

    @Override
    public String toString() {
        return "SeparatorBuilder{" +
                "isInvisible=" + isInvisible +
                ", spacing=" + spacing +
                ", uniqueId=" + uniqueId +
                '}';
    }
}
