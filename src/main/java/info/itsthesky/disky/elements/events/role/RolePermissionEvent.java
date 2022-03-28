package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;

public class RolePermissionEvent extends DiSkyEvent<RoleUpdatePermissionsEvent> {

    static {
        register("Role Permission Change", RolePermissionEvent.class, BukkitRolePermissionEvent.class,
                "[discord] [guild] role permission[s] (update|change)")
                .description("Fired when the permissions of a role changes.")
                .examples("on role permissions change:");

        SkriptUtils.registerBotValue(BukkitRolePermissionEvent.class);

        SkriptUtils.registerAuthorValue(BukkitRolePermissionEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRolePermissionEvent.class, Permission[].class,
                event -> event.getJDAEvent().getNewPermissions().toArray(new Permission[0]), 1);

        SkriptUtils.registerValue(BukkitRolePermissionEvent.class, Permission[].class,
                event -> event.getJDAEvent().getOldPermissions().toArray(new Permission[0]), -1);

        SkriptUtils.registerValue(BukkitRolePermissionEvent.class, Permission[].class,
                event -> event.getJDAEvent().getNewPermissions().toArray(new Permission[0]), 0);

        SkriptUtils.registerValue(BukkitRolePermissionEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRolePermissionEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);



    }
    public static class BukkitRolePermissionEvent extends SimpleDiSkyEvent<RoleUpdatePermissionsEvent> {
        public BukkitRolePermissionEvent(RolePermissionEvent event) {
        }
    }
}
