package info.itsthesky.disky.elements.events.bots;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;

public class GuildReadyEvent extends DiSkyEvent<net.dv8tion.jda.api.events.guild.GuildReadyEvent> {

	static {
		register("Guild Ready Event", GuildReadyEvent.class, BukkitGuildReadyEvent.class,
				"guild (ready|load[ed])")
				.description("Fired when a guild is fully loaded.");

		SkriptUtils.registerBotValue(BukkitGuildReadyEvent.class);

		SkriptUtils.registerValue(BukkitGuildReadyEvent.class, Guild.class, GuildReadyEvent::apply);
	}

	private static Guild apply(BukkitGuildReadyEvent e) {
		return e.getJDAEvent().getGuild();
	}

	public static class BukkitGuildReadyEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.guild.GuildReadyEvent> {
		public BukkitGuildReadyEvent(GuildReadyEvent event) {
			
		}
	}
	
}
