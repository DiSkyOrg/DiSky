package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;

public class RoleNameEvent extends DiSkyEvent<RoleUpdateNameEvent> {

    static {
        register("Role Name Change", RoleNameEvent.class, BukkitRoleNameEvent.class,
                "[discord] [guild] role name (update|change)")
                .description("Fired when the name of a role changes.")
                .examples("on role name change:");

        SkriptUtils.registerBotValue(BukkitRoleNameEvent.class);

        SkriptUtils.registerAuthorValue(BukkitRoleNameEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRoleNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(BukkitRoleNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(BukkitRoleNameEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(BukkitRoleNameEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(BukkitRoleNameEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);

    }
    public static class BukkitRoleNameEvent extends SimpleDiSkyEvent<RoleUpdateNameEvent> {
        public BukkitRoleNameEvent(RoleNameEvent event) {
        }
    }
}