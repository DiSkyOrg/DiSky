package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelName extends ActionProperty<GuildChannel, ChannelAction, String> {

    static {
        register(
                ChannelName.class,
                String.class,
                "channel name",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {String.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel role, String value) {
        role.getManager().setName(value).queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, String value) {
        return action.setName(value);
    }

    @Override
    public String get(GuildChannel role) {
        return role.getName();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channel name";
    }
}
