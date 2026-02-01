package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.entities.Member;
import net.itsthesky.disky.api.generator.SeeAlso;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("TimeOut Member")
@Description({"Timeout a member (temporal exclusion) for a specific duration and with an optional reason.",
        "You can either timeout UNTIL a specific date (Skript date), or FOR a specific timespan (Skript timespan).",
        "This also can be used to remove the current time out, if the bot has the permission to do so."})
@Examples({"timeout event-member for 5 minutes due to \"ur so bad\"",
        "time out event-member until {_date}",
        "stop time out of event-member"})
@Since("4.0.0")
@SeeAlso(Member.class)
public class TimeOutMember extends AsyncEffect {

    static {
        Skript.registerEffect(
                TimeOutMember.class,
                "time[( |-)]out %member% for %timespan% [(for [the reason]|due to) %-string%]",
                "time[( |-)]out %member% until %date% [(for [the reason]|due to) %-string%]",
                "(stop|remove) time[( |-)]out (from|of) %member%"
        );
    }

    private Expression<Member> exprMember;
    private Expression<Object> exprTime;
    private Expression<String> exprReason;
    private int matchedPattern;

    @Override
    public boolean init(Expression[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
        getParser().setHasDelayBefore(Kleenean.TRUE);

        this.matchedPattern = matchedPattern;
        exprMember = (Expression<Member>) expressions[0];
        if (matchedPattern == 2) return true;
        exprTime = (Expression<Object>) expressions[1];
        exprReason = (Expression<String>) expressions[2];
        return true;
    }

    @Override
    public void execute(@NotNull Event e) {
        final Member member = parseSingle(exprMember, e, null);
        if (matchedPattern == 2 && !anyNull(this, this, member)) {
            try {
                member.removeTimeout().complete();
            } catch (Exception ex) {
                DiSkyRuntimeHandler.error(ex, getNode());
            }
            return;
        }

        final Object entity = parseSingle(exprTime, e, null);
        final @Nullable String reason = parseSingle(exprReason, e, null);
        if (anyNull(this, member, entity)) {
            DiSkyRuntimeHandler.error(new IllegalArgumentException("Member or time entity is null!"), getNode());
            return;
        }

        try {
            if (matchedPattern == 0) {
                member.timeoutFor(Duration.ofMillis(((Timespan) entity).getAs(Timespan.TimePeriod.MILLISECOND)))
                        .reason(reason)
                        .complete();
            } else if (matchedPattern == 1) {
                member.timeoutUntil(Instant.ofEpochMilli(((Date) entity).getTime()))
                        .reason(reason)
                        .complete();
            }
        } catch (Exception ex) {
            DiSkyRuntimeHandler.error(ex, getNode());
        }
    }

    @Override
    public @NotNull String toString(@Nullable Event e, boolean debug) {
        return "time out " + exprMember.toString(e, debug) + " for/until " + exprTime.toString(e, debug);
    }
}
