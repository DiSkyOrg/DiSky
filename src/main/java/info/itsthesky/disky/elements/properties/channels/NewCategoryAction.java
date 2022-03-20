package info.itsthesky.disky.elements.properties.channels;

import info.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import org.jetbrains.annotations.NotNull;

public class NewCategoryAction extends GuildAction<ChannelAction> {

    static {
        register(
                NewCategoryAction.class,
                ChannelAction.class,
                "category"
        );
    }

    @Override
    protected ChannelAction create(@NotNull Guild guild) {
        return guild.createCategory("default channel");
    }

    @Override
    public String getNewType() {
        return "category";
    }

    @Override
    public Class<? extends ChannelAction> getReturnType() {
        return ChannelAction.class;
    }
}
