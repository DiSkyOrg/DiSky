package net.itsthesky.disky.elements.datastructs.structures;

import ch.njol.skript.util.Color;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.SkriptColor;
import net.itsthesky.disky.api.EmbedManager;
import net.itsthesky.disky.api.datastruct.DataStructure;
import net.itsthesky.disky.api.datastruct.DataStructureEntry;
import net.itsthesky.disky.api.datastruct.base.BasicDS;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;
import java.util.List;

@DataStructure(clazz = EmbedBuilder.class)
public class EmbedStructure implements BasicDS<EmbedBuilder> {

    @DataStructureEntry(value = "title")
    public String title;
    @DataStructureEntry(value = "url")
    public String url;

    @DataStructureEntry(value = "description")
    public String description;

    @DataStructureEntry(value = "footer")
    public String footer;
    @DataStructureEntry(value = "footer icon")
    public String footerIcon;

    @DataStructureEntry(value = "thumbnail")
    public String thumbnail;
    @DataStructureEntry(value = "image")
    public String image;

    @DataStructureEntry(value = "author")
    public String author;
    @DataStructureEntry(value = "author icon")
    public String authorIcon;
    @DataStructureEntry(value = "author url")
    public String authorUrl;

    @DataStructureEntry(value = "timestamp")
    public Date timestamp;

    @DataStructureEntry(value = "color")
    public Color color = SkriptColor.YELLOW;

    @DataStructureEntry(value = "field", subStructureType = EmbedFieldStructure.class)
    public List<MessageEmbed.Field> fields;

    @DataStructureEntry(value = "template")
    public String template;

    @Override
    public EmbedBuilder build() {
        EmbedBuilder builder = EmbedManager.getTemplate(template);
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

        if (timestamp != null) builder.setTimestamp(Instant.ofEpochMilli(timestamp.getTime()));

        if (fields != null) {
            for (MessageEmbed.Field field : fields) {
                builder.addField(field);
            }
        }

        return builder;
    }

}
