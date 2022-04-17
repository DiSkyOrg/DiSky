package info.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.NewsChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get News Channel")
@Description({"Get a news channel from a guild using its unique ID.",
        "Channels are global on discord, means different channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("news channel with id \"000\"")
public class GetNewsChannel extends BaseGetterExpression<NewsChannel> {

    static {
        register(GetNewsChannel.class,
                NewsChannel.class,
                "news channel");
    }

    @Override
    protected NewsChannel get(String id, Bot bot) {
        return bot.getInstance().getNewsChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "news channel";
    }

    @Override
    public @NotNull Class<? extends NewsChannel> getReturnType() {
        return NewsChannel.class;
    }
}
