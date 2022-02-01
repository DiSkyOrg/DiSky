package info.itsthesky.disky.elements.getters;

import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

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
