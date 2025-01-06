package info.itsthesky.disky.elements.datastructs.structures;

import info.itsthesky.disky.api.datastruct.DataStructureEntry;
import info.itsthesky.disky.api.datastruct.base.BasicDS;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbedFieldStructure implements BasicDS<MessageEmbed.Field> {

    @DataStructureEntry(value = "name")
    public String name;

    @DataStructureEntry(value = "value")
    public String value;

    @DataStructureEntry(value = "inline", optional = true)
    public boolean inline = false;

    @Override
    public MessageEmbed.Field build() {
        return new MessageEmbed.Field(name, value, inline);
    }
}
