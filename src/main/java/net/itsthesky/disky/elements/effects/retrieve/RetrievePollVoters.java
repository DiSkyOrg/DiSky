package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.elements.properties.polls.PollAnswerData;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Poll Voters")
@Description({"Retrieve all users who voted for a specific answer of a poll message.",
        "The answer can be specified by its 1-indexed position (a number) or by passing a poll answer object directly.",
        "Returns a list of users — the bot must have read access to the channel containing the poll."})
@Examples({
        "# Important: pass the Message, not the MessagePoll.",
        "# The JDA API exposes retrievePollVoters on Message, not on the poll itself.",
        "",
        "retrieve message with id \"...\" from channel with id \"...\" and store it in {_msg}",
        "set {_poll} to poll of {_msg}",
        "",
        "# By position",
        "retrieve poll voters of answer 1 of {_msg} and store them in {_voters::*}",
        "",
        "# By answer object — note we still pass {_msg}, not {_poll}",
        "loop poll answers of {_poll}:",
        "    retrieve poll voters of loop-value of {_msg} and store them in {_voters::*}",
        "    broadcast \"Answer '%answer text of loop-value%': %size of {_voters::*}% voters\""
})
@Since("4.29.0")
public class RetrievePollVoters extends AsyncEffect {

    static {
        Skript.registerEffect(
                RetrievePollVoters.class,
                "retrieve [(all|every)] [poll] voters of [poll] answer %number% (of|in|from) %message% [(with|using) [the] [bot] %-bot%] and store (them|the voters) in %~objects%",
                "retrieve [(all|every)] [poll] voters of %pollanswer% (of|in|from) %message% [(with|using) [the] [bot] %-bot%] and store (them|the voters) in %~objects%"
        );
    }

    private Expression<?> exprAnswer;
    private Expression<Message> exprMessage;
    private Expression<Bot> exprBot;
    private Expression<Object> exprResult;
    private boolean answerIsObject;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        exprAnswer = expressions[0];
        exprMessage = (Expression<Message>) expressions[1];
        exprBot = (Expression<Bot>) expressions[2];
        exprResult = (Expression<Object>) expressions[3];
        answerIsObject = matchedPattern == 1;
        return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, User[].class);
    }

    @Override
    protected void execute(@NotNull Event event) {
        final Object answerRaw = exprAnswer.getSingle(event);
        final Message message = exprMessage.getSingle(event);
        final Bot bot = Bot.fromContext(exprBot, event);
        if (answerRaw == null || message == null || bot == null)
            return;

        final long answerId;
        if (answerIsObject) {
            // Pattern 1: user passed a PollAnswerData
            final PollAnswerData answer = (PollAnswerData) answerRaw;
            answerId = answer.getId();
            if (answerId < 1) {
                DiSkyRuntimeHandler.error(new IllegalStateException(
                        "This poll answer has no Discord-assigned ID yet. It was built locally (via 'new poll answer with content ...') and the ID is only assigned once the poll is sent. To retrieve voters, fetch the answer from a sent poll first (via 'poll answers of <pollMessage>')."));
                return;
            }
        } else {
            // Pattern 0: user passed a number — but allow numeric strings too,
            // since 'discord id of <pollanswer>' returns a String. Without this,
            // a perfectly natural script like:
            //     set {_id} to discord id of loop-value
            //     retrieve poll voters of answer {_id} of {_msg} ...
            // would silently fail with a ClassCastException inside the async task.
            final long raw;
            if (answerRaw instanceof Number) {
                raw = ((Number) answerRaw).longValue();
            } else if (answerRaw instanceof String) {
                try {
                    raw = Long.parseLong(((String) answerRaw).trim());
                } catch (NumberFormatException ex) {
                    DiSkyRuntimeHandler.error(new IllegalArgumentException(
                            "Poll answer ID must be a number (got string: '" + answerRaw + "')."));
                    return;
                }
            } else {
                DiSkyRuntimeHandler.error(new IllegalArgumentException(
                        "Poll answer ID must be a number or numeric string. Got: "
                                + answerRaw.getClass().getSimpleName()));
                return;
            }
            if (raw < 1) {
                DiSkyRuntimeHandler.error(new IllegalArgumentException(
                        "Poll answer IDs are 1-indexed (first answer is 1). Got: " + raw));
                return;
            }
            answerId = raw;
        }

        // Rebind the channel through the provided bot so credentials/permissions match.
        // This mirrors how RetrieveMessage handles cross-bot message access.
        final MessageChannel channel = bot.getInstance()
                .getChannelById(MessageChannel.class, message.getChannelId());
        if (channel == null) {
            DiSkyRuntimeHandler.error(new IllegalStateException(
                    "Bot '" + bot.getName() + "' has no access to channel " + message.getChannelId() + "."));
            return;
        }

        final User[] voters;
        try {
            final List<User> result = channel.retrievePollVotersById(message.getId(), answerId).complete();
            voters = result.toArray(new User[0]);
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex);
            return;
        }

        exprResult.change(event, voters, Changer.ChangeMode.SET);
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "retrieve poll voters of " + (answerIsObject ? "" : "answer ") + exprAnswer.toString(event, debug)
                + " of " + exprMessage.toString(event, debug)
                + (exprBot != null ? " using bot " + exprBot.toString(event, debug) : "")
                + " and store them in " + exprResult.toString(event, debug);
    }
}