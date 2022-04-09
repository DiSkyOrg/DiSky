package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;

public class GuildOwnerEvent extends DiSkyEvent<GuildUpdateOwnerEvent> {

    static {
        register("Guild Owner Event", GuildOwnerEvent.class, BukkitGuildOwnerEvent.class,
                "[discord] guild owner (change|update)")
                .description("Fired when a owner of a guild changes can be used to get the old/new owner, the author and the guild.")
                .examples("on guild owner change:");


        SkriptUtils.registerBotValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class);

        SkriptUtils.registerAuthorValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class, Member.class,
                event -> event.getJDAEvent().getOldOwner(), -1);

        SkriptUtils.registerValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class, Member.class,
                event -> event.getJDAEvent().getNewOwner(), 0);

        SkriptUtils.registerValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class, Member.class,
                event -> event.getJDAEvent().getNewOwner(), 1);

        SkriptUtils.registerValue(GuildOwnerEvent.BukkitGuildOwnerEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild());

    }

    public static class BukkitGuildOwnerEvent extends SimpleDiSkyEvent<GuildUpdateOwnerEvent> {
        public BukkitGuildOwnerEvent(GuildOwnerEvent event) {
        }
    }
}
