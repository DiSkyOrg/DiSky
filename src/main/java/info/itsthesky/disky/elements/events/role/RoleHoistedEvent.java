package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent;

public class RoleHoistedEvent extends DiSkyEvent<RoleUpdateHoistedEvent> {

    static {
        register("Role Hoist Change", RoleHoistedEvent.class, BukkitRoleHoistEvent.class,
                        "[discord] [guild] role hoist[ed] (update|change)")
                .description("Fired when the hoist state of a role changes.")
                .examples("on role hoist change:");


        SkriptUtils.registerBotValue(RoleHoistedEvent.BukkitRoleHoistEvent.class);

        SkriptUtils.registerAuthorValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, Boolean.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, Boolean.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, Boolean.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleHoistedEvent.BukkitRoleHoistEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);
    }

    public static class BukkitRoleHoistEvent extends SimpleDiSkyEvent<RoleUpdateHoistedEvent> {
        public BukkitRoleHoistEvent(RoleUpdateHoistedEvent event) {
        }
    }
}