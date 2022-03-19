package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChannelChannels extends MultiplyPropertyExpression<Category, TextChannel> {

    static {
        register(
                ChannelChannels.class,
                TextChannel.class,
                "[discord] channel[s]",
                "category"
        );
    }

    @Override
    public @NotNull Class<? extends TextChannel> getReturnType() {
        return TextChannel.class;
    }

    @Override
    protected String getPropertyName() {
        return "channels";
    }

    @Override
    protected TextChannel[] convert(Category t) {
        return t.getTextChannels().toArray(new TextChannel[0]);
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "channels of " + getExpr().toString(e, debug);
    }

}
