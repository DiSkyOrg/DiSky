package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

@Name("Get Guild")
@Description({"Get a guild from a guild using its unique ID.",
        "This expression cannot be changed."})
@Examples("guild with id \"000\"")
@Since("4.0.0")
public class GetGuild extends BaseGetterExpression<Guild> {

    static {
        register(GetGuild.class,
                Guild.class,
                "guild");
    }

    @Override
    protected Guild get(String id, Bot bot) {
        return bot.getInstance().getGuildById(id);
    }

    @Override
    public String getCodeName() {
        return "guild";
    }

    @Override
    public @NotNull Class<? extends Guild> getReturnType() {
        return Guild.class;
    }
}
