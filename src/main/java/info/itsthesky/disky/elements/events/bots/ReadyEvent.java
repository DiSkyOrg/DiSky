package info.itsthesky.disky.elements.events.bots;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;

public class ReadyEvent extends DiSkyEvent<net.dv8tion.jda.api.events.session.ReadyEvent> {

	static {
		register("Ready Event", ReadyEvent.class, BukkitReadyEvent.class,
				"(ready|bot load[ed])")
				.description("Fired when a bot is fully loaded. 'guild ready' should be called before this one.");

		SkriptUtils.registerBotValue(BukkitReadyEvent.class);
	}
	
	public static class BukkitReadyEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.session.ReadyEvent> {
		public BukkitReadyEvent(ReadyEvent event) {
			
		}
	}
	
}
