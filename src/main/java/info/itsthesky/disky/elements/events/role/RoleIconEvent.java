package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;

public class RoleIconEvent extends DiSkyEvent<RoleUpdateIconEvent> {

    static {
        register("Role Icon Change", RoleIconEvent.class, BukkitRoleIconEvent.class,
                "[discord] [guild] role icon (update|change)")
                .description("Fired when the icon of a role changes.")
                .examples("on role icon change:");

        SkriptUtils.registerBotValue(BukkitRoleIconEvent.class);

        SkriptUtils.registerAuthorValue(BukkitRoleIconEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRoleIconEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue().getIconUrl(), 1);

        SkriptUtils.registerValue(BukkitRoleIconEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue().getIconUrl(), 0);

        SkriptUtils.registerValue(BukkitRoleIconEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue().getIconUrl(), -1);

        SkriptUtils.registerValue(BukkitRoleIconEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRoleIconEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);

    }
    public static class BukkitRoleIconEvent extends SimpleDiSkyEvent<RoleUpdateIconEvent> {
        public BukkitRoleIconEvent(RoleIconEvent event) {
        }
    }
}