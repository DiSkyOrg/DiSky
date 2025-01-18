package net.itsthesky.disky.elements.properties.polls.answers;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.elements.properties.polls.PollAnswerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswerText extends SimplePropertyExpression<PollAnswerData, String> {

    static {
        register(PollAnswerText.class, String.class,
                "answer text",
                "pollanswer"
        );
    }

    @Override
    public @Nullable String convert(PollAnswerData from) {
        return from.getAnswer();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll answer";
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
