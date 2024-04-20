package info.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollQuestion extends SimplePropertyExpression<MessagePoll, String> {

    static {
        register(PollQuestion.class, String.class,
                "poll question",
                "messagepoll"
        );
    }

    @Override
    public @Nullable String convert(MessagePoll from) {
        return from.getQuestion().getText();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll question";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

}
