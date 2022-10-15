package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelTopic extends ActionProperty<GuildChannel, ChannelAction, String> {

    static {
        register(
                ChannelTopic.class,
                String.class,
                "[channel] topic",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel channel, String value) {
        if (channel instanceof ForumChannel)
            return;

        ((StandardGuildMessageChannel) channel).getManager().setTopic(value).queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, String value) {
        return action.setTopic(value);
    }

    @Override
    public String get(GuildChannel channel) {
        if (channel instanceof ForumChannel)
            return ((ForumChannel) channel).getTopic();

        return ((StandardGuildMessageChannel) channel).getTopic();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel topic";
    }
}
