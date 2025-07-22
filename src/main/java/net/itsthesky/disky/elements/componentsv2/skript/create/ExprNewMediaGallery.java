package net.itsthesky.disky.elements.componentsv2.skript.create;

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

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.componentsv2.base.sub.MediaGalleryBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprNewMediaGallery extends SimpleExpression<MediaGalleryBuilder> {

    static {
        Skript.registerExpression(
                ExprNewMediaGallery.class,
                MediaGalleryBuilder.class,
                ExpressionType.COMBINED,
                "[a] new [media] gallery with [the] image[s] %fileuploads% [with [unique] id %-integer%]"
        );
    }

    private Expression<FileUpload> exprImages;
    private Expression<Integer> exprUniqueId;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        exprImages = (Expression<FileUpload>) expressions[0];
        exprUniqueId = (Expression<Integer>) expressions[1];
        return true;
    }

    @Override
    protected MediaGalleryBuilder @Nullable [] get(Event event) {
        final var values = EasyElement.parseList(exprImages, event, new FileUpload[0]);
        final var uniqueId = EasyElement.parseSingle(exprUniqueId, event, -1);

        if (values == null || values.length == 0)
            return new MediaGalleryBuilder[0];

        final var builder = new MediaGalleryBuilder(uniqueId, null);
        for (FileUpload value : values)
            builder.addItem(value);
        return new MediaGalleryBuilder[]{builder};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends MediaGalleryBuilder> getReturnType() {
        return MediaGalleryBuilder.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "new media gallery with images " + exprImages.toString(event, debug);
    }
}
