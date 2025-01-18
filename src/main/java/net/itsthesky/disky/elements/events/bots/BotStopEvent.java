package net.itsthesky.disky.elements.events.bots;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.events.session.ShutdownEvent;

public class BotStopEvent extends DiSkyEvent<ShutdownEvent> {

	static {
		register("Shutdown Event", BotStopEvent.class, BukkitShutdownEvent.class,
				"bot (shutdown|stop)")
				.description("Fired when a bot is stopped.");

		SkriptUtils.registerBotValue(BukkitShutdownEvent.class);
	}

	public static class BukkitShutdownEvent extends SimpleDiSkyEvent<ShutdownEvent> {
		public BukkitShutdownEvent(BotStopEvent event) {
			
		}
	}
	
}
