package net.itsthesky.disky.elements.events.bots;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;

public class BotLeaveEvent extends DiSkyEvent<GuildLeaveEvent> {

	static {
		register("Bot Leave Event", BotLeaveEvent.class, BukkitBotLeaveEvent.class,
				"bot [guild] (leave|left)")
				.description("Fired when any bot leave a guild.");

		SkriptUtils.registerBotValue(BukkitBotLeaveEvent.class);
		SkriptUtils.registerValue(BukkitBotLeaveEvent.class, Guild.class, BotLeaveEvent::apply);
	}

	private static Guild apply(BukkitBotLeaveEvent e) {
		return e.getJDAEvent().getGuild();
	}

	public static class BukkitBotLeaveEvent extends SimpleDiSkyEvent<GuildLeaveEvent> {
		public BukkitBotLeaveEvent(BotLeaveEvent event) {
			
		}
	}
	
}
