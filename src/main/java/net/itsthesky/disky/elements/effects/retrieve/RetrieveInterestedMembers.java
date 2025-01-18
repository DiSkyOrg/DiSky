package net.itsthesky.disky.elements.effects.retrieve;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Name("Retrieve Interested Members")
@Description("Retrieve all members who are interested in a scheduled event.")
public class RetrieveInterestedMembers extends AsyncEffect {

	static {
		Skript.registerEffect(
				RetrieveInterestedMembers.class,
				"retrieve [all] interest[ed] members of [the] [scheduled] event %scheduledevent% [using [the] [bot] %-bot%] and store (it|the members) in %~objects%"
		);
	}

	private Expression<ScheduledEvent> exprEvent;
	private Expression<Bot> exprBot;
	private Expression<Object> exprResult;

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprEvent = (Expression<ScheduledEvent>) expressions[0];
		exprBot = (Expression<Bot>) expressions[1];
		exprResult = (Expression<Object>) expressions[2];
		return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class);
	}

	@Override
	protected void execute(Event event) {
		ScheduledEvent scheduledEvent = exprEvent.getSingle(event);
		Bot bot = Bot.fromContext(exprBot, event);
		if (scheduledEvent == null || bot == null)
			return;

		scheduledEvent = bot.getInstance().getScheduledEventById(scheduledEvent.getId());
		if (scheduledEvent == null)
			return;

		final List<Member> members;
		try {
			members = scheduledEvent.retrieveInterestedMembers().complete();
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(event, ex);
			return;
		}

		exprResult.change(event, members.toArray(new Member[0]), Changer.ChangeMode.SET);
	}

	@Override
	public @NotNull String toString(Event e, boolean debug) {
		return "retrieve all interested members of scheduled event " + exprEvent.toString(e, debug)
				+ (exprBot != null ? " using bot " + exprBot.toString(e, debug) : "")
				+ " and store them in " + exprResult.toString(e, debug);
	}
}
