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
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.componentsv2.ComponentBuildException;
import net.itsthesky.disky.elements.componentsv2.base.IContainerComponentBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MediaGalleryBuilder implements IContainerComponentBuilder<MediaGallery> {

    private final List<FileUpload> sources = new ArrayList<>();
    private int uniqueId = -1;
    private @Nullable MediaGallery component = null;

    public void addItem(FileUpload source) {
        this.sources.add(source);
    }

    @Override
    public MediaGallery build() {
        if (component != null)
            return component;

        if (sources.isEmpty())
            throw new ComponentBuildException("MediaGallery must contain at least one item.");

        return MediaGallery.of(sources.stream()
                .map(MediaGalleryItem::fromFile)
                .toList());
    }

    @Override
    public void loadFrom(MediaGallery component) {
        this.sources.clear();
        // TODO: convert back to FileUpload
        this.component = component;
        this.uniqueId = component.getUniqueId();
    }

    @Override
    public String toString() {
        return "MediaGalleryBuilder{" +
                "sources=" + sources +
                ", uniqueId=" + uniqueId +
                '}';
    }
}
