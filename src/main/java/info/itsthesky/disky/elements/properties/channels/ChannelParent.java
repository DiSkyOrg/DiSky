package info.itsthesky.disky.elements.properties.channels;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.action.ActionProperty;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelParent extends ActionProperty<GuildChannel, ChannelAction, Category> {

    static {
        register(
                ChannelParent.class,
                Category.class,
                "[channel] parent",
                "channel/channelaction"
        );
    }


    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        return mode.equals(Changer.ChangeMode.SET) ? new Class[] {Category.class} : new Class[0];
    }

    @Override
    public void change(GuildChannel role, Category value, boolean async) {
        var action = ((ICategorizableChannel) role).getManager().setParent(value);

        if (async) action.complete();
        else action.queue();
    }

    @Override
    public ChannelAction change(ChannelAction action, Category value) {
        return action.setParent(value);
    }

    @Override
    public Category get(GuildChannel role, boolean async) {
        return ((ICategorizableChannel) role).getParentCategory();
    }

    @Override
    public @NotNull Class<? extends Category> getReturnType() {
        return Category.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "category parent";
    }
}
