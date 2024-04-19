package info.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PollAnswers extends SimplePropertyExpression<MessagePollBuilder, PollAnswerData> {

    static {
        register(PollAnswers.class,
                PollAnswerData.class,
                "[poll] answers",
                "messagepoll"
        );
    }

    @Override
    public PollAnswerData convert(MessagePollBuilder from) {
        return null;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll answers";
    }

    @Override
    public @NotNull Class<? extends PollAnswerData> getReturnType() {
        return PollAnswerData.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD)
            return new Class[] {PollAnswerData.class, PollAnswerData[].class};

        return new Class[0];
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta.length == 0)
            return;

        PollAnswerData[] answers = (PollAnswerData[]) delta;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD) {
            for (MessagePollBuilder poll : getExpr().getArray(event)) {
                for (PollAnswerData answer : answers) {
                    if (answer == null)
                        return;

                    poll.addAnswer(answer.getAnswer(), answer.getJDAEmote());
                }
            }
        }
    }
}
