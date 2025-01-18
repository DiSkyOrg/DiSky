package net.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Timespan;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class PollDuration extends SimplePropertyExpression<MessagePollBuilder, Timespan> {

    static {
        register(PollDuration.class,
                Timespan.class,
                "poll duration",
                "messagepollbuilder"
        );
    }

    @Override
    public @Nullable Timespan convert(MessagePollBuilder from) {
        return null;
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "poll duration";
    }

    @Override
    public @NotNull Class<? extends Timespan> getReturnType() {
        return Timespan.class;
    }

    @Override
    public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET)
            return new Class[] {Timespan.class};

        return new Class[0];
    }

    @Override
    public void change(@NotNull Event event, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        if (delta.length == 0 || delta[0] == null)
            return;

        Timespan timespan = (Timespan) delta[0];
        if (mode == Changer.ChangeMode.SET) {
            for (MessagePollBuilder poll : getExpr().getArray(event)) {
                Duration duration = Duration.ofMillis(timespan.getAs(Timespan.TimePeriod.MILLISECOND));
                poll.setDuration(duration);
            }
        }
    }
}
