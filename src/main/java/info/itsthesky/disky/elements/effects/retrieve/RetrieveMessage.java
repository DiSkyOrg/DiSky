package info.itsthesky.disky.elements.effects.retrieve;

import info.itsthesky.disky.api.skript.BaseRetrieveEffect;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.RestAction;
import org.jetbrains.annotations.NotNull;

public class RetrieveMessage extends BaseRetrieveEffect<Message, MessageChannel> {

    static {
        register(
                RetrieveMessage.class,
                "message",
                "channel"
        );
    }

    @Override
    protected RestAction<Message> retrieve(@NotNull String input, @NotNull MessageChannel entity) {
        return entity.retrieveMessageById(input);
    }

}
