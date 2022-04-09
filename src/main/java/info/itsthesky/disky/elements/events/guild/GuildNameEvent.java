package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;

public class GuildNameEvent extends DiSkyEvent<GuildUpdateNameEvent> {

    static {
        register("Guild Name Event", GuildNameEvent.class, BukkitGuildNameEvent.class,
                "[discord] guild name (update|change)")
                .description("Fired when the name of a guild is changed can be used to get the old/new name.")
                .examples("on guild name change:");


        SkriptUtils.registerBotValue(GuildNameEvent.BukkitGuildNameEvent.class);

        SkriptUtils.registerAuthorValue(GuildNameEvent.BukkitGuildNameEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildNameEvent.BukkitGuildNameEvent.class, String.class,
                event -> event.getJDAEvent().getOldValue(), -1);

        SkriptUtils.registerValue(GuildNameEvent.BukkitGuildNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 0);

        SkriptUtils.registerValue(GuildNameEvent.BukkitGuildNameEvent.class, String.class,
                event -> event.getJDAEvent().getNewValue(), 1);

        SkriptUtils.registerValue(GuildNameEvent.BukkitGuildNameEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildNameEvent extends SimpleDiSkyEvent<GuildUpdateNameEvent> {
        public BukkitGuildNameEvent(GuildNameEvent event) {
        }
    }
}
