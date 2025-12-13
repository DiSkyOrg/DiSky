package net.itsthesky.disky.elements.datastructs.structures;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.SkriptColor;
import net.itsthesky.disky.api.EmbedManager;
import net.itsthesky.disky.api.datastruct.DataStructure;
import net.itsthesky.disky.api.datastruct.DataStructureEntry;
import net.itsthesky.disky.api.datastruct.base.BasicDS;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.time.Instant;
import java.util.List;

@DataStructure(clazz = EmbedBuilder.class)
@Name("Embed Data Structure")
@Description("Create an embed with all the indicated fields. See [the wiki about Data Structures](https://docs.disky.me/latest/docs/ds/) for more infos.")
@Since("4.21.0")
@SeeAlso({EmbedBuilder.class})
public class EmbedStructure implements BasicDS<EmbedBuilder> {

    @DataStructureEntry(value = "title", description = "The title of the embed.")
    public String title;
    @DataStructureEntry(value = "url", description = "The URL of the title.", additionalInfoForAcceptedValues = "URL")
    public String url;

    @DataStructureEntry(value = "description", description = "The main description/content of the embed. Supports new lines and markdown.")
    public String description;

    @DataStructureEntry(value = "footer", description = "The footer text of the embed.")
    public String footer;
    @DataStructureEntry(value = "footer icon", description = "The footer icon URL of the embed.", additionalInfoForAcceptedValues = "URL")
    public String footerIcon;

    @DataStructureEntry(value = "thumbnail", description = "The thumbnail image URL of the embed.", additionalInfoForAcceptedValues = "Image URL")
    public String thumbnail;
    @DataStructureEntry(value = "image", description = "The main image URL of the embed.", additionalInfoForAcceptedValues = "Image URL")
    public String image;

    @DataStructureEntry(value = "author", description = "The author name of the embed.")
    public String author;
    @DataStructureEntry(value = "author icon", description = "The author icon URL of the embed. An author is required to set an icon.", additionalInfoForAcceptedValues = "Image URL")
    public String authorIcon;
    @DataStructureEntry(value = "author url", description = "The author URL of the embed. An author is required to set a URL.", additionalInfoForAcceptedValues = "URL")
    public String authorUrl;

    @DataStructureEntry(value = "timestamp", description = "The timestamp of the embed.")
    public Date timestamp;

    @DataStructureEntry(value = "color", description = "The color of the embed.")
    public Color color = SkriptColor.YELLOW;

    @DataStructureEntry(value = "field", subStructureType = EmbedFieldStructure.class)
    public List<MessageEmbed.Field> fields;

    @DataStructureEntry(value = "template", description = "The template name to use for this embed.")
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
