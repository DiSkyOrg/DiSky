package info.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagePoll extends SimplePropertyExpression<Message, net.dv8tion.jda.api.entities.messages.MessagePoll> {

    static {
        register(
                MessagePoll.class,
                net.dv8tion.jda.api.entities.messages.MessagePoll.class,
                "[discord] [message] poll",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll";
    }

    @Override
    public @Nullable net.dv8tion.jda.api.entities.messages.MessagePoll convert(Message original) {
        return original.getPoll();
    }

    @Override
    public @NotNull Class<? extends net.dv8tion.jda.api.entities.messages.MessagePoll> getReturnType() {
        return net.dv8tion.jda.api.entities.messages.MessagePoll.class;
    }
}
