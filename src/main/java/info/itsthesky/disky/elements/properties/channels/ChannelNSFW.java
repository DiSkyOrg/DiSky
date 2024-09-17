package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelNSFW extends ActionProperty<GuildChannel, ChannelAction, Boolean> {

    static {
        register(
                ChannelNSFW.class,
                Boolean.class,
                "[channel] nsfw",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {Boolean.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel role, Boolean value, boolean async) {
        var action = ((StandardGuildMessageChannel) role).getManager().setNSFW(value);

        if (async) action.complete();
        else action.queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, Boolean value) {
        return action.setNSFW(value);
    }

    @Override
    public Boolean get(GuildChannel role, boolean async) {
        return ((StandardGuildMessageChannel) role).isNSFW();
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel topic";
    }
}
