package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

public class GuildLogEntryEvent extends DiSkyEvent<GuildAuditLogEntryCreateEvent> {

	static {
		register("Guild Log Entry Create Event", GuildLogEntryEvent.class, BukkitGuildLogEntryEvent.class,
				"[discord] guild log [entry] create")
				.description("Fired when a new log entry is created in a guild.")
				.examples("on guild log entry create:");

		SkriptUtils.registerBotValue(BukkitGuildLogEntryEvent.class);
		SkriptUtils.registerAuthorValue(BukkitGuildLogEntryEvent.class, e -> e.getJDAEvent().getGuild());

		SkriptUtils.registerValue(BukkitGuildLogEntryEvent.class, AuditLogEntry.class,
				event -> event.getJDAEvent().getEntry(), 0);

		SkriptUtils.registerValue(BukkitGuildLogEntryEvent.class, Guild.class,
				event -> event.getJDAEvent().getGuild(), 0);

	}

	public static class BukkitGuildLogEntryEvent extends SimpleDiSkyEvent<GuildAuditLogEntryCreateEvent> {
		
		public BukkitGuildLogEntryEvent(GuildLogEntryEvent event) {
			
		}
	}
	
}
