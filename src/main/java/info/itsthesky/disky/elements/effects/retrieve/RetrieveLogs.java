package info.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import info.itsthesky.disky.api.skript.BaseMultipleRetrieveEffect;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Logs")
@Description("Retrieve the audit logs of a guild.")
@Examples("retrieve audit logs from event-guild and store it in {_logs}")
@Since("4.11.0")
public class RetrieveLogs extends BaseMultipleRetrieveEffect<List<AuditLogEntry>, Guild> {

    static {
        register(
                RetrieveLogs.class,
                "[audit] log[s] [entries]",
                "guild"
        );
    }

    @Override
    protected RestAction<List<AuditLogEntry>> retrieve(@NotNull Guild entity) {
        return entity.retrieveAuditLogs();
    }

}
