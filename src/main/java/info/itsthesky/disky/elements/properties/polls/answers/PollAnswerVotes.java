package info.itsthesky.disky.elements.properties.polls.answers;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.elements.properties.polls.PollAnswerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswerVotes extends SimplePropertyExpression<PollAnswerData, Number> {

    static {
        register(PollAnswerVotes.class, Number.class,
                "answer votes",
                "pollanswer"
        );
    }

    @Override
    public @Nullable Number convert(PollAnswerData from) {
        return from.getVotes();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll votes";
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
