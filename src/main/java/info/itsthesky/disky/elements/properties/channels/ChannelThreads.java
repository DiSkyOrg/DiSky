package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

public class ChannelThreads extends MultiplyPropertyExpression<GuildChannel, ThreadChannel> {

    static {
        register(
                ChannelThreads.class,
                ThreadChannel.class,
                "threads",
                "channel"
        );
    }

    @Override
    public Class<? extends ThreadChannel> getReturnType() {
        return ThreadChannel.class;
    }

    @Override
    protected String getPropertyName() {
        return "threads";
    }

    @Override
    protected ThreadChannel[] convert(GuildChannel t) {
        return t
                .getGuild()
                .getThreadChannels()
                .stream()
                .filter(channel -> channel.getParentChannel().getId().equalsIgnoreCase(t.getId()))
                .toArray(ThreadChannel[]::new);
    }
}
