package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.channel.attribute.IPositionableChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelPosition extends ActionProperty<GuildChannel, ChannelAction, Number> {

    static {
        register(
                ChannelPosition.class,
                Number.class,
                "[channel] position",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {Number.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel role, Number value) {
        ((IPositionableChannel) role).getManager().setPosition(value.intValue()).queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, Number value) {
        return action.setPosition(value.intValue());
    }

    @Override
    public Number get(GuildChannel role) {
        return ((IPositionableChannel) role).getPosition();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel position";
    }
}
