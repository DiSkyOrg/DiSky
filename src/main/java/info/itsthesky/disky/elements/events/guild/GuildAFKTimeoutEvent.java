package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateAfkTimeoutEvent;

public class GuildAFKTimeoutEvent extends DiSkyEvent<GuildUpdateAfkTimeoutEvent> {

    static {
        register("Guild AFK Timeout Event", GuildAFKTimeoutEvent.class, BukkitAFKTimeoutEvent.class,
                "[discord] guild afk timeout (change|update)")
                .description("Fired when a afk timeout of a guild changes can be used to get the old/new timeout value, the author and the guild.")
                .examples("on guild afk timeout change:");


        SkriptUtils.registerBotValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class);

        SkriptUtils.registerAuthorValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class, Guild.Timeout.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class, Guild.Timeout.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class, Guild.Timeout.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(GuildAFKTimeoutEvent.BukkitAFKTimeoutEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitAFKTimeoutEvent extends SimpleDiSkyEvent<GuildUpdateAfkTimeoutEvent> {
        public BukkitAFKTimeoutEvent(GuildAFKTimeoutEvent event) {
        }
    }
}
