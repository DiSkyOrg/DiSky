package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.Utils;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

@Name("TimeOut Member")
@Description({"Timeout a member (temporal exclusion) for a specific duration and with an optional reason.",
"You can either timeout UNTIL a specific date (Skript date), or FOR a specific timespan (Skript timespan).",
"This also can be used to remove the current time out, if the bot has the permission to do so."})
@Examples({"timeout event-member for 5 minutes due to \"ur so bad\"",
"time out event-member until {_date}",
"stop time out of event-member"})
public class TimeOutMember extends SpecificBotEffect {

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
	public boolean initEffect(Expression[] expressions, int matchedPattern, Kleenean kleenean, ParseResult parseResult) {
		this.matchedPattern = matchedPattern;
		exprMember = (Expression<Member>) expressions[0];
		if (matchedPattern == 2) return true;
		exprTime = (Expression<Object>) expressions[1];
		exprReason = (Expression<String>) expressions[2];
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, Bot bot) {
		final Member member = parseSingle(exprMember, e, null);
		if (matchedPattern == 2 && !anyNull(this, this, member)) {
			Utils.catchAction(member.removeTimeout(),
					v -> restart(), ex -> {
						DiSky.getErrorHandler().exception(e, ex);
						restart();
					});
			return;
		}

		final Object entity = parseSingle(exprTime, e, null);
		final @Nullable String reason = parseSingle(exprReason, e, null);
		if (anyNull(this, this, member, entity)) {
			restart();
			return;
		}
		if (matchedPattern == 0) {
			Utils.catchAction(member
							.timeoutFor(Duration.ofMillis(((Timespan) entity).getMilliSeconds()))
							.reason(reason),
					v -> restart(), ex -> {
						DiSky.getErrorHandler().exception(e, ex);
						restart();
					});
		} else if (matchedPattern == 1) {
			Utils.catchAction(member
							.timeoutUntil(Instant.ofEpochMilli(((Date) entity).getTimestamp()))
							.reason(reason),
					v -> restart(), ex -> {
						DiSky.getErrorHandler().exception(e, ex);
						restart();
			});
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "time out "+exprMember.toString(e, debug)+" for/until " + exprTime.toString(e, debug);
	}
}
