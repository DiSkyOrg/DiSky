package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.events.automod.AutoModRuleCreateEvent;

public class GuildAutoModRuleCreateEvent extends DiSkyEvent<AutoModRuleCreateEvent> {

    static {
        register("AutoMod Rule Create", GuildAutoModRuleCreateEvent.class, BukkitAutoModRuleCreateEvent.class,
                "[discord] automod rule create")
                .examples("Fired when an automod rule was updated.")
                .examples("on automod rule update:");

        SkriptUtils.registerBotValue(BukkitAutoModRuleCreateEvent.class);

        SkriptUtils.registerValue(BukkitAutoModRuleCreateEvent.class, Guild.class,
                event -> event.getJDAEvent().getRule().getGuild(), 0);

        SkriptUtils.registerValue(BukkitAutoModRuleCreateEvent.class, AutoModRuleCreateEvent.class,
                SimpleDiSkyEvent::getJDAEvent, 0);

        ReflectEventExpressionFactory.registerEventExpression(
                "rule name", BukkitAutoModRuleCreateEvent.class,
                AutoModRule.class, event -> event.getJDAEvent().getRule());
    }

    public static class BukkitAutoModRuleCreateEvent extends SimpleDiSkyEvent<AutoModRuleCreateEvent> {
        public BukkitAutoModRuleCreateEvent(GuildAutoModRuleCreateEvent event) {
        }
    }
}
