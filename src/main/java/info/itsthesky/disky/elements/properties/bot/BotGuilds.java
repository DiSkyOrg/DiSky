package info.itsthesky.disky.elements.properties.bot;

import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Guild;

public class BotGuilds extends MultiplyPropertyExpression<Bot, Guild> {

    static {
        register(
                BotGuilds.class,
                Guild.class,
                "guilds",
                "bot"
        );
    }

    @Override
    public Class<? extends Guild> getReturnType() {
        return Guild.class;
    }

    @Override
    protected String getPropertyName() {
        return "guilds";
    }

    @Override
    protected Guild[] convert(Bot t) {
        return t.getInstance().getGuilds().toArray(new Guild[0]);
    }
}
