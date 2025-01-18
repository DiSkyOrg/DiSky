package net.itsthesky.disky.elements.properties.polls;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.emojis.Emote;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NewPollAnswer extends SimpleExpression<PollAnswerData> {

    static {
        Skript.registerExpression(
                NewPollAnswer.class,
                PollAnswerData.class,
                ExpressionType.COMBINED,
                "[new] [poll] answer [with [the] content] %string% [[and] with [the] [emoji] %-emote%]"
        );
    }

    private Expression<String> exprContent;
    private Expression<Emote> exprEmote;

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprContent = (Expression<String>) expressions[0];
        exprEmote = (Expression<Emote>) expressions[1];
        return true;
    }

    @Override
    protected @Nullable PollAnswerData @NotNull [] get(@NotNull Event event) {
        String content = exprContent.getSingle(event);
        Emote emote = exprEmote == null ? null : exprEmote.getSingle(event);
        if (content == null)
            return new PollAnswerData[0];

        return new PollAnswerData[] {
                new PollAnswerData(content, emote)
        };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends PollAnswerData> getReturnType() {
        return PollAnswerData.class;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        return "new poll answer with content " + exprContent.toString(event, debug)
                + (exprEmote == null ? "" : " and with emoji " + exprEmote.toString(event, debug));
    }
}
