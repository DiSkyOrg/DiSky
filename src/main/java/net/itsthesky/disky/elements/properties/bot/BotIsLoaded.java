package net.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.DiSky;
import org.jetbrains.annotations.NotNull;

@Name("BotIsLoaded")
@Description("Check if a bot with the specified name is currently loaded in DiSky.")
@Examples({"if \"MyBot\" is loaded on discord:",
        "\treply with \"Bot is online!\""})
@Since("4.0.0")
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
