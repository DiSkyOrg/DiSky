package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Text Channel")
@Description({"Get a text channel from a guild using its unique ID.",
        "Text channel are global on discord, means two role from two different guild could never have the same ID.",
        "This expression cannot be changed."})
@Examples("role with id \"000\"")
public class GetTextChannel extends BaseGetterExpression<TextChannel> {

    static {
        register(GetTextChannel.class,
                TextChannel.class,
                "text channel");
    }

    @Override
    protected TextChannel get(String id, Bot bot) {
        return bot.getInstance().getTextChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "text channel";
    }

    @Override
    public @NotNull Class<? extends TextChannel> getReturnType() {
        return TextChannel.class;
    }
}
