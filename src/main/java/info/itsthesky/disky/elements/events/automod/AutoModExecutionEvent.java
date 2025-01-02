package info.itsthesky.disky.elements.events.automod;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

public class AutoModExecutionEvent extends DiSkyEvent<net.dv8tion.jda.api.events.automod.AutoModExecutionEvent> {

    static {
        register("AutoMod Execution Event", AutoModExecutionEvent.class, BukkitAutoModExecutionEvent.class,
                "[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .examples("on automod execute:");


        SkriptUtils.registerBotValue(AutoModExecutionEvent.BukkitAutoModExecutionEvent.class);

        SkriptUtils.registerValue(AutoModExecutionEvent.BukkitAutoModExecutionEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(AutoModExecutionEvent.BukkitAutoModExecutionEvent.class, GuildMessageChannelUnion.class,
                event -> event.getJDAEvent().getChannel(), 0);

        SkriptUtils.registerValue(AutoModExecutionEvent.BukkitAutoModExecutionEvent.class, AutoModResponse.class,
                event -> event.getJDAEvent().getResponse(), 0);

    }

    public static class BukkitAutoModExecutionEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.automod.AutoModExecutionEvent> {
        public BukkitAutoModExecutionEvent(AutoModExecutionEvent event) {
        }
    }
}
