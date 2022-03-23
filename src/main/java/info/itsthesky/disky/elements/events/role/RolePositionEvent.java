package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdatePositionEvent;

public class RolePositionEvent extends DiSkyEvent<RoleUpdatePositionEvent> {

    static {
        register("Role Position Change", RolePositionEvent.class, BukkitRolePositionEvent.class,
                "[discord] [guild] role position (update|change)")
                .description("Fired when the position of a role changes.")
                .examples("on role position change:");

        SkriptUtils.registerBotValue(BukkitRolePositionEvent.class);

        SkriptUtils.registerAuthorValue(BukkitRolePositionEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRolePositionEvent.class, Integer.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(BukkitRolePositionEvent.class, Integer.class,
                event -> event.getJDAEvent().getOldValue(), 0);

        SkriptUtils.registerValue(BukkitRolePositionEvent.class, Integer.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(BukkitRolePositionEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRolePositionEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);

    }
    public static class BukkitRolePositionEvent extends SimpleDiSkyEvent<RoleUpdatePositionEvent> {
        public BukkitRolePositionEvent(RolePositionEvent event) {
        }
    }
}