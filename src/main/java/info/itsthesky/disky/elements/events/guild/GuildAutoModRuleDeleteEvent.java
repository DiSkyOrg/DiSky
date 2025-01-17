package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.events.automod.AutoModRuleDeleteEvent;

public class GuildAutoModRuleDeleteEvent extends DiSkyEvent<AutoModRuleDeleteEvent> {

    static {
        register("AutoMod Rule Delete", GuildAutoModRuleDeleteEvent.class, BukkitAutoModRuleDeleteEvent.class,
                "[discord] automod rule delete")
                .examples("Fired when an automod rule was deleted.")
                .examples("on automod rule delete:");

        SkriptUtils.registerBotValue(BukkitAutoModRuleDeleteEvent.class);

        SkriptUtils.registerValue(BukkitAutoModRuleDeleteEvent.class, Guild.class,
                event -> event.getJDAEvent().getRule().getGuild(), 0);

        SkriptUtils.registerValue(BukkitAutoModRuleDeleteEvent.class, AutoModRuleDeleteEvent.class,
                SimpleDiSkyEvent::getJDAEvent, 0);

        ReflectEventExpressionFactory.registerEventExpression(
                "rule name", BukkitAutoModRuleDeleteEvent.class,
                AutoModRule.class, event -> event.getJDAEvent().getRule());
    }

    public static class BukkitAutoModRuleDeleteEvent extends SimpleDiSkyEvent<AutoModRuleDeleteEvent> {
        public BukkitAutoModRuleDeleteEvent(GuildAutoModRuleDeleteEvent event) {
        }
    }
}
