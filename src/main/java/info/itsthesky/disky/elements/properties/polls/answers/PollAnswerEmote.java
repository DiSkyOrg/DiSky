package info.itsthesky.disky.elements.properties.polls.answers;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.emojis.Emote;
import info.itsthesky.disky.elements.properties.polls.PollAnswerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswerEmote extends SimplePropertyExpression<PollAnswerData, Emote> {

    static {
        register(PollAnswerEmote.class, Emote.class,
                "answer emote",
                "pollanswer"
        );
    }

    @Override
    public @Nullable Emote convert(PollAnswerData from) {
        return from.getEmote();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll emote";
    }

    @Override
    public @NotNull Class<? extends Emote> getReturnType() {
        return Emote.class;
    }
}
