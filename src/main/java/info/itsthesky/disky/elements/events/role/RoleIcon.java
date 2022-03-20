package info.itsthesky.disky.elements.events.role;

import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.events.specific.LogEvent;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.role.update.RoleUpdateIconEvent;
import org.jetbrains.annotations.NotNull;

public class RoleIcon extends DiSkyEvent<RoleUpdateIconEvent> {

    static {
        DiSkyEvent.register("Role Hoist Change", RoleIcon.class, EvtRoleIcon.class,
                        "[discord] [guild] role hoist[ed] (update|change)")
                .description("Fired when the hoist state of a role changes.")
                .examples("on role hoist change:");


        EventValues.registerEventValue(EvtRoleIcon.class, String.class, new Getter<String, EvtRoleIcon>() {
            @Override
            public String get(@NotNull EvtRoleIcon event) {
                return event.getJDAEvent().getNewValue().getIconUrl();
            }
        }, 1);

        EventValues.registerEventValue(EvtRoleIcon.class, String.class, new Getter<String, EvtRoleIcon>() {
            @Override
            public String get(@NotNull EvtRoleIcon event) {
                return event.getJDAEvent().getOldValue().getIconUrl();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleIcon.class, String.class, new Getter<String, EvtRoleIcon>() {
            @Override
            public String get(@NotNull EvtRoleIcon event) {
                return event.getJDAEvent().getOldValue().getIconUrl();
            }
        }, -1);

        EventValues.registerEventValue(EvtRoleIcon.class, Guild.class, new Getter<Guild, EvtRoleIcon>() {
            @Override
            public Guild get(@NotNull EvtRoleIcon event) {
                return event.getJDAEvent().getGuild();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleIcon.class, Role.class, new Getter<Role, EvtRoleIcon>() {
            @Override
            public Role get(@NotNull EvtRoleIcon event) {
                return event.getJDAEvent().getRole();
            }
        }, 0);

        EventValues.registerEventValue(EvtRoleIcon.class, Bot.class, new Getter<Bot, EvtRoleIcon>() {
            @Override
            public Bot get(@NotNull EvtRoleIcon event) {
                return DiSky.getManager().fromJDA(event.getJDAEvent().getJDA());
            }
        }, 0);

    }

    public static class EvtRoleIcon extends SimpleDiSkyEvent<RoleUpdateIconEvent> implements LogEvent {
        public EvtRoleIcon(RoleIcon event) { }

        @Override
        public User getActionAuthor() {
            return getJDAEvent().getGuild().retrieveAuditLogs().complete().get(0).getUser();
        }
    }

}