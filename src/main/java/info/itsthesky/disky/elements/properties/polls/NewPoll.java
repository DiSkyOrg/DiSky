package info.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.utils.messages.MessagePollBuilder;
import net.dv8tion.jda.api.utils.messages.MessagePollData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewPoll extends SimpleExpression<MessagePollBuilder> {

    static {
        Skript.registerExpression(
                NewPoll.class,
                MessagePollBuilder.class,
                ExpressionType.SIMPLE,
                "new [message] poll [with [the] title] %string% [1:with multi[ple]( |-)(choice|answer)[s]]"
        );
    }

    private Expression<String> exprTitle;
    private boolean multipleChoice;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprTitle = (Expression<String>) expressions[0];
        multipleChoice = parseResult.hasTag("1");
        return true;
    }

    @Override
    protected MessagePollBuilder @NotNull [] get(@NotNull Event event) {
        String title = exprTitle.getSingle(event);
        if (title == null)
            return new MessagePollBuilder[0];

        return new MessagePollBuilder[] {
                MessagePollData.builder(title)
                        .setMultiAnswer(multipleChoice)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends MessagePollBuilder> getReturnType() {
        return MessagePollBuilder.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "new poll with title " + exprTitle.toString(event, debug) + (multipleChoice ? " with multiple choice" : "");
    }

}
