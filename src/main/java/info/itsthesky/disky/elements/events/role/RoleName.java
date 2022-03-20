package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.RoleUpdateNameEvent;
import org.jetbrains.annotations.NotNull;

public class RoleName extends DiSkyEvent<RoleUpdateNameEvent> {

    static {
        register("Role Delete", RoleName.class, EvtRoleName.class,
                "[discord] [guild] role name (update|change)")
                .description("Fired when the name of a role changes.")
                .examples("on role name change:");


        EventValues.registerEventValue(EvtRoleName.class, Role.class, new Getter<Role, EvtRoleName>() {
            @Override
            public Role get(@NotNull EvtRoleName event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleName.class, Guild.class, new Getter<Guild, EvtRoleName>() {
            @Override
            public Guild get(@NotNull EvtRoleName event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleName.class, Bot.class, new Getter<Bot, EvtRoleName>() {
            @Override
            public Bot get(@NotNull EvtRoleName event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleName extends SimpleDiSkyEvent<RoleUpdateNameEvent> implements LogEvent {
        public EvtRoleName(RoleName event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}