package net.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.conditions.base.PropertyCondition;
import net.itsthesky.disky.DiSky;
import org.jetbrains.annotations.NotNull;

public class BotIsLoaded extends PropertyCondition<String> {

    static {
        register(
                BotIsLoaded.class,
                PropertyType.BE,
                "[been] loaded (in|on|from|over) discord",
                "string"
        );
    }

    @Override
    public boolean check(@NotNull String name) {
        return DiSky.getManager().exist(name);
    }

    @Override
    protected String getPropertyName() {
        return "loaded";
    }
}
