package info.itsthesky.disky.elements.events.guild;

import ch.njol.skript.util.Date;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import info.itsthesky.disky.core.SkriptUtils;
import info.itsthesky.disky.elements.events.member.MemberTimeoutEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;

import static info.itsthesky.disky.core.SkriptUtils.convertDateTime;

public class GuildAutoModExecutionEvent extends DiSkyEvent<net.dv8tion.jda.api.events.automod.AutoModExecutionEvent> {

    static {
        register("AutoMod Execution Event", GuildAutoModExecutionEvent.class, BukkitAutoModExecutionEvent.class,
                "[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .examples("on automod execute:");

        SkriptUtils.registerBotValue(GuildAutoModExecutionEvent.BukkitAutoModExecutionEvent.class);

        SkriptUtils.registerValue(GuildAutoModExecutionEvent.BukkitAutoModExecutionEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(GuildAutoModExecutionEvent.BukkitAutoModExecutionEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel(), 0);

        SkriptUtils.registerValue(GuildAutoModExecutionEvent.BukkitAutoModExecutionEvent.class, AutoModResponse.class,
                event -> event.getJDAEvent().getResponse(), 0);

        ReflectEventExpressionFactory.registerEventExpression(
                "alert message id", GuildAutoModExecutionEvent.BukkitAutoModExecutionEvent.class,
                String.class, event -> event.getJDAEvent().getAlertMessageId());
    }

    public static class BukkitAutoModExecutionEvent extends SimpleDiSkyEvent<net.dv8tion.jda.api.events.automod.AutoModExecutionEvent> {
        public BukkitAutoModExecutionEvent(GuildAutoModExecutionEvent event) {
        }
    }
}
