package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class RetrieveMessage extends BaseRetrieveEffect<Message, BaseGuildMessageChannel> {

    static {
        register(
                RetrieveMessage.class,
                "message",
                "textchannel/channel/thread"
        );
    }

    @Override
    protected RestAction<Message> retrieve(@NotNull String input, @NotNull BaseGuildMessageChannel entity) {
        return entity.retrieveMessageById(input);
    }

}
