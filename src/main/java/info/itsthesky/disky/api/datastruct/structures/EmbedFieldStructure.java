package info.itsthesky.disky.api.datastruct.structures;

import info.itsthesky.disky.api.datastruct.DataStructure;
import info.itsthesky.disky.api.datastruct.DataStructureEntry;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import net.dv8tion.jda.api.entities.MessageEmbed;

@DataStructure(value = "embed field", clazz = MessageEmbed.Field.class, canBeCreated = false)
public class EmbedFieldStructure implements DataStruct<MessageEmbed.Field> {

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
