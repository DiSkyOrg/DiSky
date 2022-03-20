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
import org.jetbrains.annotations.NotNull;

public class RoleDelete extends DiSkyEvent<RoleDeleteEvent> {

    static {
        register("Role Delete", RoleDelete.class, EvtRoleDelete.class,
                        "[discord] [guild] role delete")
                .description("Fired when a role is deleted from a guild")
                .examples("on role delete:");


        EventValues.registerEventValue(EvtRoleDelete.class, Role.class, new Getter<Role, EvtRoleDelete>() {
            @Override
            public Role get(@NotNull EvtRoleDelete event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleDelete.class, Guild.class, new Getter<Guild, EvtRoleDelete>() {
            @Override
            public Guild get(@NotNull EvtRoleDelete event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleDelete.class, Bot.class, new Getter<Bot, EvtRoleDelete>() {
            @Override
            public Bot get(@NotNull EvtRoleDelete event) {
               return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleDelete extends SimpleDiSkyEvent<RoleDeleteEvent> implements LogEvent {
        public EvtRoleDelete(RoleDelete event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}