package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;

@Name("Guild Voice Channels")
@Description("Gets all voice channels of a guild.")
@Examples("all voice channels of event-guild")
public class GuildVoiceChannels extends MultipleGuildProperty<VoiceChannel> {

    static {
        register(GuildVoiceChannels.class,
                VoiceChannel.class,
                "[all] voice[( |-)]channels");
    }

    @Override
    public VoiceChannel[] converting(Guild guild) {
        return guild.getVoiceChannels().toArray(new VoiceChannel[0]);
    }

}
