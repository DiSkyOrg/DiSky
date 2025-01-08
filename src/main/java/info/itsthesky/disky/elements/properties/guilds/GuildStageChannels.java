package info.itsthesky.disky.elements.properties.guilds;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;

@Name("All Guild Stage Channels")
@Description("Returns all stage channels of a guild.")
@Examples("all stage channels of event-guild")
public class GuildStageChannels extends MultipleGuildProperty<StageChannel> {

    static {
        register(GuildStageChannels.class,
                StageChannel.class,
                "[all] stage[( |-)]channels");
    }

    @Override
    public StageChannel[] converting(Guild guild) {
        return guild.getStageChannels().toArray(new StageChannel[0]);
    }

}
