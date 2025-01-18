package net.itsthesky.disky.elements.properties.messages;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagePoll extends SimplePropertyExpression<Object, net.dv8tion.jda.api.entities.messages.MessagePoll> {

    static {
        register(
                MessagePoll.class,
                net.dv8tion.jda.api.entities.messages.MessagePoll.class,
                "[discord] [message] poll",
                "message/messagecreatebuilder"
        );
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll";
    }

    @Override
    public @Nullable net.dv8tion.jda.api.entities.messages.MessagePoll convert(Object entity) {
        if (entity instanceof Message)
            return ((Message) entity).getPoll();

        return null;
    }

    @Override
    public @NotNull Class<? extends net.dv8tion.jda.api.entities.messages.MessagePoll> getReturnType() {
        return net.dv8tion.jda.api.entities.messages.MessagePoll.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[] {MessagePollBuilder.class};

        return new Class[0];
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta.length == 0 || delta[0] == null)
            return;

        MessagePollBuilder poll = (MessagePollBuilder) delta[0];
        if (mode == Changer.ChangeMode.SET) {
            for (Object entity : getExpr().getArray(event)) {
                if (entity instanceof MessageCreateBuilder) {
                    MessageCreateBuilder message = (MessageCreateBuilder) entity;
                    message.setPoll(poll.build());
                }
            }
        }
    }
}
