package net.itsthesky.disky.elements.properties;

import net.itsthesky.disky.api.skript.reflects.ReflectClassFactory;
import net.itsthesky.disky.api.skript.reflects.state.SkriptStateRegistry;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.requests.RestAction;

import static net.itsthesky.disky.api.skript.reflects.ReflectClassFactory.register;

public final class DynamicElements {

    public static void registerLogs() {
        register( "logentry", "logged user", User.class, "log[ged] (user|author)", AuditLogEntry::getUser,
                new ReflectClassFactory.Documentation("Logged User", "The user who triggered the log entry.",
                        "logged user of event-logentry", "4.11.0"));
        register( "logentry", "logged guild", Guild.class, "log[ged] guild", AuditLogEntry::getGuild,
                new ReflectClassFactory.Documentation("Logged Guild", "The guild where the log entry has been triggered.",
                        "logged guild of event-logentry", "4.11.0"));
        register( "logentry", "logged id", String.class, "log[ged] id", AuditLogEntry::getId,
                new ReflectClassFactory.Documentation("Logged ID", "The ID of the log entry.",
                        "logged id of event-logentry", "4.11.0"));
        register( "logentry", "logged action", ActionType.class, "log[ged] action [type]", AuditLogEntry::getType,
                new ReflectClassFactory.Documentation("Logged Action", "The action type of the log entry.",
                        "logged action of event-logentry", "4.11.0"));
        register( "logentry", "logged reason", String.class, "log[ged] reason", AuditLogEntry::getReason,
                new ReflectClassFactory.Documentation("Logged Reason", "The action reason of the log entry.",
                        "logged reason of event-logentry", "4.17.2"));
    }

    public static void registerThreadProperties() {

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "locked", ThreadChannel::isLocked,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setLocked(value)
                            : entity.getManager().setLocked(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "archived", ThreadChannel::isArchived,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setArchived(value)
                            : entity.getManager().setArchived(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "pinned", ThreadChannel::isPinned,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setPinned(value)
                            : entity.getManager().setPinned(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

        SkriptStateRegistry.register(
                ThreadChannel.class, "threadchannel",
                "invitable", ThreadChannel::isInvitable,
                (entity, async, value) -> {
                    final RestAction<Void> rest = value != null
                            ? entity.getManager().setInvitable(value)
                            : entity.getManager().setInvitable(false);

                    if (async) rest.complete();
                    else rest.queue();
                }
        );

    }
}
