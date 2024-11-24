package info.itsthesky.disky.api.datastruct.structures;

import ch.njol.skript.util.Color;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.SkriptColor;
import ch.njol.skript.util.Timespan;
import info.itsthesky.disky.api.datastruct.DataStructure;
import info.itsthesky.disky.api.datastruct.DataStructureEntry;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

@DataStructure(value = "embed", clazz = EmbedBuilder.class)
public class EmbedStructure {

    @DataStructureEntry(value = "title", optional = true)
    public String title;
    @DataStructureEntry(value = "url", optional = true)
    public String url;

    @DataStructureEntry(value = "description", optional = true)
    public String description;

    @DataStructureEntry(value = "footer", optional = true)
    public String footer;
    @DataStructureEntry(value = "footer icon", optional = true)
    public String footerIcon;

    @DataStructureEntry(value = "thumbnail", optional = true)
    public String thumbnail;
    @DataStructureEntry(value = "image", optional = true)
    public String image;

    @DataStructureEntry(value = "author", optional = true)
    public String author;
    @DataStructureEntry(value = "author icon", optional = true)
    public String authorIcon;
    @DataStructureEntry(value = "author url", optional = true)
    public String authorUrl;

    @DataStructureEntry(value = "timestamp", optional = true)
    public Date timestamp;

    @DataStructureEntry(value = "color", optional = true)
    public Color color = SkriptColor.YELLOW;

    public EmbedBuilder build() {
        EmbedBuilder builder = new EmbedBuilder();
        if (title != null) builder.setTitle(title);
        if (description != null) builder.setDescription(description);
        if (color != null) builder.setColor(SkriptUtils.convert(color));

        if (footer != null) {
            if (footerIcon != null) {
                builder.setFooter(footer, footerIcon);
            } else {
                builder.setFooter(footer);
            }
        }

        if (author != null) {
            if (authorIcon != null) {
                if (authorUrl != null) {
                    builder.setAuthor(author, authorUrl, authorIcon);
                } else {
                    builder.setAuthor(author, null, authorIcon);
                }
            } else {
                if (authorUrl != null) {
                    builder.setAuthor(author, authorUrl);
                } else {
                    builder.setAuthor(author);
                }
            }
        }

        if (thumbnail != null) builder.setThumbnail(thumbnail);
        if (image != null) builder.setImage(image);

        if (timestamp != null) builder.setTimestamp(Instant.ofEpochMilli(timestamp.getTimestamp()));

        return builder;
    }

}
