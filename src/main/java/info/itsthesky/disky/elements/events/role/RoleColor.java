package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateColorEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class RoleColor extends DiSkyEvent<RoleUpdateColorEvent> {

    static {
        register("Role Color Change", RoleColor.class, EvtRoleColor.class,
                        "[discord] [guild] role color (update|change)")
                .description("Fired when the color of a role changes.")
                .examples("on role color change:");


        EventValues.registerEventValue(EvtRoleColor.class, Color.class, new Getter<Color, EvtRoleColor>() {
            @Override
            public Color get(@NotNull EvtRoleColor event) {
                return event.getJDAEvent().getNewColor();
            }
        }, 1);

        EventValues.registerEventValue(EvtRoleColor.class, Color.class, new Getter<Color, EvtRoleColor>() {
            @Override
            public Color get(@NotNull EvtRoleColor event) {
                return event.getJDAEvent().getOldColor();
            }
        }, -1);

        EventValues.registerEventValue(EvtRoleColor.class, Color.class, new Getter<Color, EvtRoleColor>() {
            @Override
            public Color get(@NotNull EvtRoleColor event) {
                return event.getJDAEvent().getOldColor();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleColor.class, Guild.class, new Getter<Guild, EvtRoleColor>() {
            @Override
            public Guild get(@NotNull EvtRoleColor event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleColor.class, Role.class, new Getter<Role, EvtRoleColor>() {
            @Override
            public Role get(@NotNull EvtRoleColor event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleColor.class, Bot.class, new Getter<Bot, EvtRoleColor>() {
            @Override
            public Bot get(@NotNull EvtRoleColor event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleColor extends SimpleDiSkyEvent<RoleUpdateColorEvent> implements LogEvent {
        public EvtRoleColor(RoleColor event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}