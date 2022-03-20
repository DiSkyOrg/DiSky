package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateHoistedEvent;
import org.jetbrains.annotations.NotNull;

public class RoleHoisted extends DiSkyEvent<RoleUpdateHoistedEvent> {

    static {
        register("Role Hoist Change", RoleHoisted.class, EvtRoleHoisted.class,
                        "[discord] [guild] role hoist[ed] (update|change)")
                .description("Fired when the hoist state of a role changes.")
                .examples("on role hoist change:");


        EventValues.registerEventValue(EvtRoleHoisted.class, Boolean.class, new Getter<Boolean, EvtRoleHoisted>() {
            @Override
            public Boolean get(@NotNull EvtRoleHoisted event) {
                return event.getJDAEvent().getNewValue();
            }
        }, 1);

        EventValues.registerEventValue(EvtRoleHoisted.class, Boolean.class, new Getter<Boolean, EvtRoleHoisted>() {
            @Override
            public Boolean get(@NotNull EvtRoleHoisted event) {
                return event.getJDAEvent().getNewValue();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleHoisted.class, Boolean.class, new Getter<Boolean, EvtRoleHoisted>() {
            @Override
            public Boolean get(@NotNull EvtRoleHoisted event) {
                return event.getJDAEvent().getOldValue();
            }
        }, -1);

        EventValues.registerEventValue(EvtRoleHoisted.class, Guild.class, new Getter<Guild, EvtRoleHoisted>() {
            @Override
            public Guild get(@NotNull EvtRoleHoisted event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleHoisted.class, Role.class, new Getter<Role, EvtRoleHoisted>() {
            @Override
            public Role get(@NotNull EvtRoleHoisted event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleHoisted.class, Bot.class, new Getter<Bot, EvtRoleHoisted>() {
            @Override
            public Bot get(@NotNull EvtRoleHoisted event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleHoisted extends SimpleDiSkyEvent<RoleUpdateHoistedEvent> implements LogEvent {
        public EvtRoleHoisted(RoleHoisted event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}