package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class RoleCreateEvent extends DiSkyEvent<net.dv8tion.jda.api.events.role.RoleCreateEvent> {

    static {
        register("Role Create", RoleCreateEvent.class, BukkitRoleCreateEvent.class,
                "[discord] [guild] role create[d]")
                .description("Fired when a role is created in a guild")
                .examples("on role create:");


        SkriptUtils.registerBotValue(RoleCreateEvent.BukkitRoleCreateEvent.class);

        SkriptUtils.registerAuthorValue(RoleCreateEvent.BukkitRoleCreateEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleCreateEvent.BukkitRoleCreateEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleCreateEvent.BukkitRoleCreateEvent.class, Role.class,
                event -> event.getJDAEvent().getRole());
    }

    public static class BukkitRoleCreateEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.role.RoleCreateEvent> {
        public BukkitRoleCreateEvent(RoleCreateEvent event) {
        }
    }
}