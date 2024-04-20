package info.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.changers.MultipleChangeablePropertyExpression;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.messages.MessagePoll;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class PollAnswers extends MultipleChangeablePropertyExpression<Object, PollAnswerData> {

    static {
        register(PollAnswers.class,
                PollAnswerData.class,
                "[poll] answers",
                "messagepollbuilder/messagepoll"
        );
    }

    @Override
    protected PollAnswerData[] convert(Object entity) {
        if (entity instanceof MessagePoll)
            return ((MessagePoll) entity)
                    .getAnswers()
                    .stream()
                    .map(PollAnswerData::new)
                    .toArray(PollAnswerData[]::new);

        return new PollAnswerData[0];
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
    public void change(@NotNull Event event, Object @NotNull [] delta, Bot bot, Changer.@NotNull ChangeMode mode) {
        if (delta.length == 0)
            return;

        PollAnswerData[] answers = (PollAnswerData[]) delta;
        if (mode == Changer.ChangeMode.SET || mode == Changer.ChangeMode.ADD) {
            for (Object entity : getExpr().getArray(event)) {
                if (entity instanceof MessagePollBuilder) {
                    final MessagePollBuilder poll = (MessagePollBuilder) entity;
                    for (PollAnswerData answer : answers) {
                        if (answer == null)
                            return;

                        poll.addAnswer(answer.getAnswer(), answer.getJDAEmote());
                    }
                }
            }
        }
    }
}
