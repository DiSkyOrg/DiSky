package net.itsthesky.disky.elements.events.guild;

import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.automod.AutoModResponse;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.automod.*;

public class GuildAutoModExecutionEvent extends DiSkyEvent<AutoModExecutionEvent> {

    static {
        register("AutoMod Execution", GuildAutoModExecutionEvent.class, BukkitAutoModExecutionEvent.class,
                "[discord] automod (execution|execute)")
                .description("Fired when an automated automod response has been triggered through an automod Rule. Can be used to get the channel, user content, keyword that was found, the automod response and the id of the automod rule, the user, the id of the message which triggered the rule, the guild it occurred in, and the id of the alert message sent to the alert channel (if configured).")
                .examples("on automod execute:");

        SkriptUtils.registerBotValue(BukkitAutoModExecutionEvent.class);

        SkriptUtils.registerValue(BukkitAutoModExecutionEvent.class, Guild.class,
                event -> event.getJDAEvent().getGuild(), 0);

        SkriptUtils.registerValue(BukkitAutoModExecutionEvent.class, Channel.class,
                event -> event.getJDAEvent().getChannel(), 0);

        SkriptUtils.registerValue(BukkitAutoModExecutionEvent.class, AutoModResponse.class,
              event -> event.getJDAEvent().getResponse(), 0);

        SkriptUtils.registerValue(BukkitAutoModExecutionEvent.class, AutoModExecutionEvent.class,
                SimpleDiSkyEvent::getJDAEvent, 0);

        ReflectEventExpressionFactory.registerEventExpression(
                "alert message id", BukkitAutoModExecutionEvent.class,
                String.class, event -> event.getJDAEvent().getAlertMessageId());

        ReflectEventExpressionFactory.registerEventExpression(
                "rule id", BukkitAutoModExecutionEvent.class,
                String.class, event -> event.getJDAEvent().getRuleId());

        ReflectEventExpressionFactory.registerEventExpression(
                "moderated user", BukkitAutoModExecutionEvent.class,
                User.class, event -> event.getJDAEvent().getJDA().getUserById(event.getJDAEvent().getUserId()));

    }

    public static class BukkitAutoModExecutionEvent extends SimpleDiSkyEvent<AutoModExecutionEvent> {
        public BukkitAutoModExecutionEvent(GuildAutoModExecutionEvent event) {
        }
    }
}
