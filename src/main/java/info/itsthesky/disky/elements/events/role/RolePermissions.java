package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePermissionsEvent;
import org.jetbrains.annotations.NotNull;

public class RolePermissions extends DiSkyEvent<RoleUpdatePermissionsEvent> {

    static {
        DiSkyEvent.register("Role Permissions Change", RolePermissions.class, EvtRolePermissions.class,
                        "[discord] [guild] role (perms|permissions) (update|change)")
                .description("Fired when the permission of a role changes.")
                .examples("on role permissions change:");

        EventValues.registerEventValue(EvtRolePermissions.class, Permission[].class, new Getter<Permission[], EvtRolePermissions>() {
            @Override
            public Permission[] get(@NotNull EvtRolePermissions event) {
                return event.getJDAEvent().getOldPermissions().toArray(new Permission[0]);
            }
        }, -1);

        EventValues.registerEventValue(EvtRolePermissions.class, Permission[].class, new Getter<Permission[], EvtRolePermissions>() {
            @Override
            public Permission[] get(@NotNull EvtRolePermissions event) {
                return event.getJDAEvent().getNewPermissions().toArray(new Permission[0]);
            }
        }, 1);

        EventValues.registerEventValue(EvtRolePermissions.class, Permission[].class, new Getter<Permission[], EvtRolePermissions>() {
            @Override
            public Permission[] get(@NotNull EvtRolePermissions event) {
                return event.getJDAEvent().getNewPermissions().toArray(new Permission[0]);
            }
        }, 0);

        EventValues.registerEventValue(EvtRolePermissions.class, Guild.class, new Getter<Guild, EvtRolePermissions>() {
            @Override
            public Guild get(@NotNull EvtRolePermissions event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRolePermissions.class, Role.class, new Getter<Role, EvtRolePermissions>() {
            @Override
            public Role get(@NotNull EvtRolePermissions event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRolePermissions.class, Bot.class, new Getter<Bot, EvtRolePermissions>() {
            @Override
            public Bot get(@NotNull EvtRolePermissions event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRolePermissions extends SimpleDiSkyEvent<RoleUpdatePermissionsEvent> implements LogEvent {
        public EvtRolePermissions(RolePermissions event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}