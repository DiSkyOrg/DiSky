package info.itsthesky.disky.elements.properties;

import info.itsthesky.disky.api.skript.ReflectClassFactory;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import static info.itsthesky.disky.api.skript.ReflectClassFactory.register;

public final class ConstLogs {

    public static void registerAll() {

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

    }
}
