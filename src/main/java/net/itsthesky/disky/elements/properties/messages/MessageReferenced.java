package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageReferenced extends SimplePropertyExpression<Message, Message> {

    static {
        register(
                MessageReferenced.class,
                Message.class,
                "[discord] [message] referenc(ing|ed) message",
                "message"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "referenced message";
    }

    @Override
    public @Nullable Message convert(Message message) {
        return message.getReferencedMessage();
    }

    @Override
    public @NotNull Class<? extends Message> getReturnType() {
        return Message.class;
    }
}
