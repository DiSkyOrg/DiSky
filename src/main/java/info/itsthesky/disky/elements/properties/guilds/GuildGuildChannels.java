package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

@Name("All Guild Guild Channels")
@Description("Get every guild channel in the guild, including text, voice, stage, news, and thread channels.")
@Examples("guild channels of event-guild")
public class GuildGuildChannels extends MultipleGuildProperty<GuildChannel> {

    static {
        register(GuildGuildChannels.class,
                GuildChannel.class,
                "[all] guild[( |-)]channels");
    }

    @Override
    public GuildChannel[] converting(Guild guild) {
        return guild.getChannels().toArray(new GuildChannel[0]);
    }

}
