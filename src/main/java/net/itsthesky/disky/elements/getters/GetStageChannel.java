package net.itsthesky.disky.elements.getters;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.channel.concrete.StageChannel;
import org.jetbrains.annotations.NotNull;

@Name("Get Stage Channel")
@Description({"Get a stage channel from a guild using its unique ID.",
        "Channels are global on discord, means different channels cannot have the same ID.",
        "This expression cannot be changed."})
@Examples("stage channel with id \"000\"")
@Since("4.0.0")
public class GetStageChannel extends BaseGetterExpression<StageChannel> {

    static {
        register(GetStageChannel.class,
                StageChannel.class,
                "stage channel");
    }

    @Override
    protected StageChannel get(String id, Bot bot) {
        return bot.getInstance().getStageChannelById(id);
    }

    @Override
    public String getCodeName() {
        return "stage channel";
    }

    @Override
    public @NotNull Class<? extends StageChannel> getReturnType() {
        return StageChannel.class;
    }
}
