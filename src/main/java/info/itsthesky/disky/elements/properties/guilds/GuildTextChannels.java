package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

@Name("All Guild Text Channels")
@Description("Returns all text channels of a guild.")
@Examples("all text channels of event-guild")
public class GuildTextChannels extends MultipleGuildProperty<TextChannel> {

    static {
        register(GuildTextChannels.class,
                TextChannel.class,
                "[all] text[( |-)]channels");
    }

    @Override
    public TextChannel[] converting(Guild guild) {
        return guild.getTextChannels().toArray(new TextChannel[0]);
    }
}
