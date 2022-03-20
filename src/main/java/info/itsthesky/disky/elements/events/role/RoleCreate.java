package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import org.jetbrains.annotations.NotNull;

public class RoleCreate extends DiSkyEvent<RoleCreateEvent> {

    static {
        register("Role Create", RoleCreate.class, EvtRoleCreate.class,
                "[discord] [guild] role create[d]")
                .description("Fired when a role is created in a guild")
                .examples("on role create:");


        EventValues.registerEventValue(EvtRoleCreate.class, Role.class, new Getter<Role, EvtRoleCreate>() {
            @Override
            public Role get(@NotNull EvtRoleCreate event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleCreate.class, Guild.class, new Getter<Guild, EvtRoleCreate>() {
            @Override
            public Guild get(@NotNull EvtRoleCreate event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleCreate.class, Bot.class, new Getter<Bot, EvtRoleCreate>() {
            @Override
            public Bot get(@NotNull EvtRoleCreate event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleCreate extends SimpleDiSkyEvent<RoleCreateEvent> implements LogEvent {
        public EvtRoleCreate(RoleCreate event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}