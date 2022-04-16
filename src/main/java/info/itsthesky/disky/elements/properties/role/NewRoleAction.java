package info.itsthesky.disky.elements.properties.role;

import info.itsthesky.disky.api.skript.action.GuildAction;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.restaction.RoleAction;
import org.jetbrains.annotations.NotNull;

public class NewRoleAction extends GuildAction<RoleAction> {

    static {
        register(
                NewRoleAction.class,
                RoleAction.class,
                "role"
        );
    }

    @Override
    protected RoleAction create(@NotNull Guild guild) {
        return guild.createRole();
    }

    @Override
    public String getNewType() {
        return "role";
    }

    @Override
    public Class<? extends RoleAction> getReturnType() {
        return RoleAction.class;
    }
}
