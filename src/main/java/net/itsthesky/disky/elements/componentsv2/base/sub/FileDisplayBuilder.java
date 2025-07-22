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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.core.JDAUtils;
import net.itsthesky.disky.elements.componentsv2.base.IContainerComponentBuilder;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
public class FileDisplayBuilder implements IContainerComponentBuilder<FileDisplay> {

    private FileUpload source;
    private boolean spoiler;
    private int uniqueId = -1;

    private @Nullable FileDisplay original;

    public FileDisplayBuilder(FileUpload source, boolean spoiler, int uniqueId) {
        this.source = source;
        this.spoiler = spoiler;
        this.uniqueId = uniqueId;
    }

    @Override
    public FileDisplay build() {
        return original == null ? FileDisplay.fromFile(source).withSpoiler(spoiler) : original;
    }

    @Override
    public void loadFrom(FileDisplay component) {
        this.original = component;
        this.spoiler = component.isSpoiler();
        this.uniqueId = component.getUniqueId();
    }

    @Override
    public String toString() {
        return "FileDisplayBuilder{" + "source='" + source + '\'' + ", spoiler=" + spoiler + ", uniqueId=" + uniqueId + '}';
    }

    public String getFileName() {
        if (original != null) {
            final var url = original.getUrl();
            final var fileName = JDAUtils.getFileNameFromUrl(url);
            return fileName != null ? fileName : url;
        }

        return source.getName();
    }
}
