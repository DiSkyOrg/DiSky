package info.itsthesky.disky.elements.properties;

import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import static info.itsthesky.disky.api.skript.ReflectClassFactory.register;

public final class ConstLogs {

    public static void registerAll() {

        register(AuditLogEntry.class, "logentry", "logged user", User.class, "log[ged] (user|author)", AuditLogEntry::getUser);
        register(AuditLogEntry.class, "logentry", "logged guild", Guild.class, "log[ged] guild", AuditLogEntry::getGuild);
        register(AuditLogEntry.class, "logentry", "logged id", String.class, "log[ged] id", AuditLogEntry::getId);
        register(AuditLogEntry.class, "logentry", "logged action", ActionType.class, "log[ged] action [type]", AuditLogEntry::getType);

    }
}
