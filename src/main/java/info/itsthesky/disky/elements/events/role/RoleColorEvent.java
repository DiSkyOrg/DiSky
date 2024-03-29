package info.itsthesky.disky.elements.events.role;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import ch.njol.skript.util.Color;

public class RoleColorEvent extends DiSkyEvent<RoleUpdateColorEvent> {

    static {
        register("Role Color Change", RoleColorEvent.class, BukkitRoleColorEvent.class,
                        "[discord] [guild] role color (update|change)")
                .description("Fired when the color of a role changes.")
                .examples("on role color change:");


        SkriptUtils.registerBotValue(RoleCreateEvent.BukkitRoleCreateEvent.class);

        SkriptUtils.registerAuthorValue(RoleCreateEvent.BukkitRoleCreateEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleColorEvent.BukkitRoleColorEvent.class, Color.class,
                event -> SkriptUtils.convert(event.getJDAEvent().getNewColor()), 1);

        SkriptUtils.registerValue(RoleColorEvent.BukkitRoleColorEvent.class, Color.class,
                event -> SkriptUtils.convert(event.getJDAEvent().getNewColor()), 1);

        SkriptUtils.registerValue(RoleColorEvent.BukkitRoleColorEvent.class, Color.class,
                event -> SkriptUtils.convert(event.getJDAEvent().getOldColor()), 1);

        SkriptUtils.registerValue(RoleColorEvent.BukkitRoleColorEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

        SkriptUtils.registerValue(RoleColorEvent.BukkitRoleColorEvent.class, Role.class,
                event -> event.getJDAEvent().getRole(), 0);
    }

    public static class BukkitRoleColorEvent extends SimpleDiSkyEvent<RoleUpdateColorEvent> {
        public BukkitRoleColorEvent(RoleColorEvent event) {
        }
    }
}