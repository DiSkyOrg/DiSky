package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelRegion extends ActionProperty<GuildChannel, ChannelAction, Region> {

    static {
        register(
                ChannelRegion.class,
                Region.class,
                "[channel] region",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {Region.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel role, Region value) {
        ((AudioChannel) role).getManager().setRegion(value).queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, Region value) {
        Skript.warning("You cannot change the voice channel region before its discord creation!");
        return action;
    }

    @Override
    public Region get(GuildChannel role) {
        return ((AudioChannel) role).getRegion();
    }

    @Override
    public @NotNull Class<? extends Region> getReturnType() {
        return Region.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel region";
    }
}
