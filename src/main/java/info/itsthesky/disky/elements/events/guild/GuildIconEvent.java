package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.entities.channel.attribute.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateIconEvent;

public class GuildIconEvent extends DiSkyEvent<GuildUpdateIconEvent> {

    static {
        register("Guild Icon Event", GuildIconEvent.class, BukkitGuildIconEvent.class,
                "[discord] guild icon (change|update)")
                .description("Fired when the icon of a guild changes can be used to get the old/new icon, the author and the guild.")
                .examples("on guild icon change:");


        SkriptUtils.registerBotValue(GuildIconEvent.BukkitGuildIconEvent.class);

        SkriptUtils.registerAuthorValue(GuildIconEvent.BukkitGuildIconEvent.class, e -> e.getJDAEvent().getGuild());

        SkriptUtils.registerValue(GuildIconEvent.BukkitGuildIconEvent.class, String.class,
                event -> event.getJDAEvent().getOldIconUrl(), -1);

        SkriptUtils.registerValue(GuildIconEvent.BukkitGuildIconEvent.class, String.class,
                event -> event.getJDAEvent().getNewIconUrl(), 0);

        SkriptUtils.registerValue(GuildIconEvent.BukkitGuildIconEvent.class, String.class,
                event -> event.getJDAEvent().getNewIconUrl(), 1);

        SkriptUtils.registerValue(GuildIconEvent.BukkitGuildIconEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

    }

    public static class BukkitGuildIconEvent extends SimpleDiSkyEvent<GuildUpdateIconEvent> {
        public BukkitGuildIconEvent(GuildIconEvent event) {
        }
    }
}
