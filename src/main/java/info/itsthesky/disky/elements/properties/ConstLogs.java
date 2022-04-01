package info.itsthesky.disky.elements.properties;

import info.itsthesky.disky.api.PropertiesFactory;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public final class ConstLogs extends PropertiesFactory {

    public static void register() {
        if (1 == 1)
            return;
        register(AuditLogEntry.class, User.class, "log[ged] (user|author)", AuditLogEntry::getUser);
        register(AuditLogEntry.class, Guild.class, "log[ged] guild", AuditLogEntry::getGuild);
        register(AuditLogEntry.class, String.class, "log[ged] id", AuditLogEntry::getId);
        register(AuditLogEntry.class, ActionType.class, "log[ged] action [type]", AuditLogEntry::getType);
    }

}
