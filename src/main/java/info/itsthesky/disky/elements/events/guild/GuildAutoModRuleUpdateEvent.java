package info.itsthesky.disky.elements.events.guild;

import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.api.skript.reflects.ReflectEventExpressionFactory;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.automod.AutoModRule;
import net.dv8tion.jda.api.events.automod.AutoModRuleUpdateEvent;

public class GuildAutoModRuleUpdateEvent extends DiSkyEvent<AutoModRuleUpdateEvent> {

    static {
        register("AutoMod Rule Update", GuildAutoModRuleUpdateEvent.class, BukkitAutoModRuleUpdateEvent.class,
                "[discord] automod rule update")
                .examples("Fired when an automod rule was updated.")
                .examples("on automod rule update:");

        SkriptUtils.registerBotValue(BukkitAutoModRuleUpdateEvent.class);

        SkriptUtils.registerValue(BukkitAutoModRuleUpdateEvent.class, Guild.class,
                event -> event.getJDAEvent().getRule().getGuild(), 0);

        SkriptUtils.registerValue(BukkitAutoModRuleUpdateEvent.class, AutoModRuleUpdateEvent.class,
                SimpleDiSkyEvent::getJDAEvent, 0);

        ReflectEventExpressionFactory.registerEventExpression(
                "rule name", BukkitAutoModRuleUpdateEvent.class,
                AutoModRule.class, event -> event.getJDAEvent().getRule());
    }

    public static class BukkitAutoModRuleUpdateEvent extends SimpleDiSkyEvent<AutoModRuleUpdateEvent> {
        public BukkitAutoModRuleUpdateEvent(GuildAutoModRuleUpdateEvent event) {
        }
    }
}
