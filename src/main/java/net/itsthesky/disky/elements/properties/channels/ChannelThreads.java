package net.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

@Name("Threads of Channel / Guild")
@Description("Gets the threads of a specific forum/text channel or a guild.")
@Examples("threads of event-guild")
@Since("4.0.0, 4.4.4 (threads of forum channel)")
public class ChannelThreads extends MultiplyPropertyExpression<Object, ThreadChannel> {

    static {
        register(
                ChannelThreads.class,
                ThreadChannel.class,
                "threads",
                "forumchannel/textchannel/guild"
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
    protected ThreadChannel[] convert(Object obj) {
        if (obj instanceof Guild)
            return ((Guild) obj).getThreadChannels().toArray(new ThreadChannel[0]);

        if (!(obj instanceof IThreadContainer)) {
            Skript.error("Cannot get threads from a channel that is not a thread container!");
            return new ThreadChannel[0];
        }
        return ((IThreadContainer) obj).getThreadChannels().toArray(new ThreadChannel[0]);
    }
}
