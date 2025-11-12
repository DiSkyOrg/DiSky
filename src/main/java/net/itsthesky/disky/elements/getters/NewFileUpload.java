package net.itsthesky.disky.elements.getters;

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
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.FileUpload;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.reflects.ReflectChangeablePropertyFactory;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

@Name("New File Upload")
@Description({"Create a new file upload from various sources: local file path, URL, attachment, or image.",
        "You can optionally specify a custom file name and whether to mark it as spoiler."})
@Examples({"set {_upload} to file upload from local file \"plugins/MyPlugin/data.txt\"",
        "set {_upload} to file upload from url \"https://example.com/image.png\" with name \"custom.png\"",
        "set {_upload} to file upload from attachment {_attachment} with spoiler true"})
@Since("4.0.0")
public class NewFileUpload extends SimpleExpression<FileUpload> {

    static {
        final var suffix = "[(with name|named) %-string%] [with spoiler %-boolean%]";
        final var patterns = new ArrayList<String>();

        // From local file
        patterns.add("[new] file (data|upload) from [local] file %string% " + suffix);
        // From URL
        patterns.add("[new] file (data|upload) from ur(l|i) %string% " + suffix);
        // From attachment
        patterns.add("[new] file (data|upload) from attachment %attachment% " + suffix);
        // If SkImage2 is installed, from image
        if (DiSky.isSkImageInstalled())
            patterns.add("[new] file (data|upload) from image %image% " + suffix);

        Skript.registerExpression(
                NewFileUpload.class,
                FileUpload.class,
                ExpressionType.COMBINED,
                patterns.toArray(new String[0])
        );

        ReflectChangeablePropertyFactory.registerChangeable(
                "fileupload", "file name",
                FileUpload.class,
                String.class, "file name",
                FileUpload::getName,
                mode -> mode == Changer.ChangeMode.SET ? new Class[]{String.class} : null,

                (fileUpload, changeData) -> {
                    if (changeData.getMode() == Changer.ChangeMode.SET) {
                        final var newName = changeData.getFirstDelta();
                        if (newName != null) {
                            fileUpload.setName(newName);
                        }
                    }
                    return null;
                }
        );
    }

    private Node node;
    private FileSource fileSource;
    private Expression<Object> exprSource;
    private Expression<String> exprName;
    private Expression<Boolean> exprSpoiler;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        node = getParser().getNode();
        fileSource = FileSource.values()[matchedPattern];

        exprSource = (Expression<Object>) expressions[0];
        exprName = (Expression<String>) expressions[1];
        exprSpoiler = (Expression<Boolean>) expressions[2];

        return true;
    }

    @Override
    protected FileUpload @Nullable [] get(Event event) {
        final var source = EasyElement.parseSingle(exprSource, event, null);
        var fileName = EasyElement.parseSingle(exprName, event, null);
        final var spoiler = EasyElement.parseSingle(exprSpoiler, event, false);
        if (source == null) {
            DiSkyRuntimeHandler.exprNotSet(node, exprSource);
            return new FileUpload[0];
        }

        @Nullable FileUpload upload = null;
        switch (fileSource) {
            case IMAGE -> {
                final var image = (BufferedImage) source;

                final var baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(image, "png", baos);
                } catch (final Exception e) {
                    DiSkyRuntimeHandler.error(e, node);
                    return new FileUpload[0];
                }

                final var hash = Integer.toHexString(image.hashCode());
                fileName = (fileName != null ? fileName : "image-" + hash + ".png");
                try {
                    upload = FileUpload.fromData(baos.toByteArray(), fileName);
                } catch (final Exception e) {
                    DiSkyRuntimeHandler.error(e, node);
                    return new FileUpload[0];
                }
            }
            case ATTACHMENT -> {
                if (!(source instanceof Message.Attachment attachment)) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException("Expected a FileUpload for attachment, got: " + source.getClass().getSimpleName()), node, false);
                    return new FileUpload[0];
                }
//                if (attachment.isEphemeral()) {
//                    DiSkyRuntimeHandler.error(new IllegalArgumentException("Cannot upload ephemeral attachments"), node, false);
//                    return new FileUpload[0];
//                }

                fileName = (fileName != null ? fileName : attachment.getFileName());
                try {
                    var stream = attachment.getProxy().download().get();
                    if (stream == null) {
                        DiSkyRuntimeHandler.error(new IllegalArgumentException("Failed to download attachment: " + attachment.getFileName()), node, false);
                        return new FileUpload[0];
                    }

                    upload = FileUpload.fromData(stream, fileName);
                } catch (final Exception e) {
                    DiSkyRuntimeHandler.error(e, node);
                    return new FileUpload[0];
                }
            }
            case URL -> {
                final var url = source.toString();
                if (url.isBlank()) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException("URL cannot be blank"), node, false);
                    return new FileUpload[0];
                }
                try {
                    final var stream = new URL(url).openStream();

                    var foundFileName = url.substring(url.lastIndexOf('/') + 1);
                    if (foundFileName.isBlank())
                        foundFileName = "file-" + Integer.toHexString(url.hashCode()) + ".txt";

                    fileName = (fileName != null ? fileName : foundFileName);
                    upload = FileUpload.fromData(stream, fileName);
                } catch (final MalformedURLException e) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException("Invalid URL: " + url, e), node, false);
                    return new FileUpload[0];
                } catch (final Exception e) {
                    DiSkyRuntimeHandler.error(e, node);
                    return new FileUpload[0];
                }
            }
            case LOCAL_FILE -> {
                final var filePath = source.toString();
                if (filePath.isBlank()) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException("File path cannot be blank"), node, false);
                    return new FileUpload[0];
                }
                final var file = new java.io.File(filePath);
                if (!file.exists() || !file.isFile()) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException("File does not exist: " + filePath), node, false);
                    return new FileUpload[0];
                }

                fileName = (fileName != null ? fileName : file.getName());
                upload = FileUpload.fromData(file);
            }
        }

        if (upload == null || fileName == null)
            return new FileUpload[0];

        upload = (spoiler ? upload.asSpoiler() : upload)
                .setName(fileName);
        return new FileUpload[]{upload};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends FileUpload> getReturnType() {
        return FileUpload.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        final var suffix = (exprName != null ? " with name " + exprName.toString(event, debug) : "")
                + (exprSpoiler != null ? " with spoiler " + exprSpoiler.toString(event, debug) : "");
        final var qualifier = switch (fileSource) {
            case LOCAL_FILE -> "local file";
            case URL -> "url";
            case ATTACHMENT -> "attachment";
            case IMAGE -> "image";
        };
        return "new file data from " + qualifier + " " + exprSource.toString(event, debug) + suffix;
    }

    public enum FileSource {
        LOCAL_FILE,
        URL,
        ATTACHMENT,
        IMAGE
    }

}
