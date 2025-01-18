package net.itsthesky.disky.elements.events.role;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

public class RoleDeleteEvent extends DiSkyEvent<net.dv8tion.jda.api.events.role.RoleDeleteEvent> {

    static {
        register("Role Delete", RoleDeleteEvent.class, BukkitRoleDeleteEvent.class,
                        "[discord] [guild] role delete")
                .description("Fired when a role is deleted from a guild.")
                .examples("on role delete:");


        SkriptUtils.registerBotValue(RoleDeleteEvent.BukkitRoleDeleteEvent.class);

        SkriptUtils.registerAuthorValue(RoleDeleteEvent.BukkitRoleDeleteEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleDeleteEvent.BukkitRoleDeleteEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleDeleteEvent.BukkitRoleDeleteEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);
    }

    public static class BukkitRoleDeleteEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.role.RoleDeleteEvent> {
        public BukkitRoleDeleteEvent(RoleDeleteEvent event) {
        }
    }
}