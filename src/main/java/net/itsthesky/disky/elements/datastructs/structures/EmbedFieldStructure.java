package net.itsthesky.disky.elements.datastructs.structures;

import net.itsthesky.disky.api.datastruct.DataStructureEntry;
import net.itsthesky.disky.api.datastruct.base.BasicDS;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedFieldStructure implements BasicDS<MessageEmbed.Field> {

    @DataStructureEntry(value = "name", optional = false, description = "The name/title of the field.")
    public String name;

    @DataStructureEntry(value = "value", optional = false, description = "The value/content of the field. Can contains new lines and markdown.")
    public String value;

    @DataStructureEntry(value = "inline", description = "Whether the field is inline or not.")
    public Boolean inline = false;

    @Override
    public MessageEmbed.Field build() {
        return new MessageEmbed.Field(name, value, inline);
    }
}
