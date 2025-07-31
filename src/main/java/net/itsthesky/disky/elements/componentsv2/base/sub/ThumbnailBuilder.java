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
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.componentsv2.base.ISectionAccessoryBuilder;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ThumbnailBuilder implements ISectionAccessoryBuilder<Thumbnail> {

    private FileUpload source;
    private int uniqueId;

    private @Nullable Thumbnail component = null;

    @Override
    public void loadFrom(Thumbnail component) {
        this.component = component;
        // TODO: handle deserialization of the source
        this.uniqueId = component.getUniqueId();
    }

    @Override
    public Thumbnail build() {
        if (component != null)
            return component;

        return Thumbnail.fromFile(source);
    }

    @Override
    public String toString() {
        return "ThumbnailBuilder{" +
                "uniqueId=" + uniqueId +
                ", component=" + component +
                '}';
    }
}
