package info.itsthesky.disky.elements.sections.message;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessagePoll extends SimplePropertyExpression<MessageCreateBuilder, MessagePollBuilder> {

    static {
        register(
                MessagePoll.class,
                MessagePollBuilder.class,
                "[discord] [message] poll",
                "message"
        );
    }

    @Override
    public @Nullable MessagePollBuilder convert(MessageCreateBuilder from) {
        return null;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll";
    }

    @Override
    public @NotNull Class<? extends MessagePollBuilder> getReturnType() {
        return MessagePollBuilder.class;
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
            for (MessageCreateBuilder message : getExpr().getArray(event)) {
                message.setPoll(poll.build());
            }
        }
    }
}
