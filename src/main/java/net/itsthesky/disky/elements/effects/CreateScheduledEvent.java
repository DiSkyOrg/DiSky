package net.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.itsthesky.disky.api.generator.SeeAlso;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.skript.util.Date;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.itsthesky.disky.api.skript.EasyElement.anyNull;
import static net.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Create Scheduled Event")
@Description({"Create a new scheduled event in a specific channel or at a specific place.",
"If you use the second pattern (without channel), you'll have to specify the guild where the event will be created, and a start & end date.",
"If you use the first pattern (with channel), you'll just have to specific the channel itself and a start date.",
"Scheduled events currently only support stage & voice channels."})
@Examples({
		"create scheduled event named \"Let's Talk Together\" in stage channel with id \"000\" at (5 hours after now) and store it in {_event}",
		"create scheduled event named \"Concerto\" at \"6 routes of XXX\" starting (1 hour after now) and ending (5 hours after now) in event-guild and store it in {_event}"
})
@Since("4.0.0")
@SeeAlso({Guild.class, ScheduledEvent.class})
public class CreateScheduledEvent extends AsyncEffect {

	static {
		Skript.registerEffect(
				CreateScheduledEvent.class,
				"create [a] [new] scheduled event (with name|named) %string% in %guildchannel% at %date% and store (it|the event) in %~objects%",
				"create [a] [new] scheduled event (with name|named) %string% at %string% starting [at] %date% [and] ending [at] %date% in %guild% and store (it|the event) in %~objects%"
		);
	}

	private Expression<String> exprName;

	private Expression<GuildChannel> exprChannel;
	private Expression<Date> exprDate;

	private Expression<String> exprPlace;
	private Expression<Date> exprStart;
	private Expression<Date> exprEnd;
	private Expression<Guild> exprGuild;

	private Expression<Object> exprResult;

	private boolean isPlace = false;

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		isPlace = i == 1;
		exprName = (Expression<String>) expressions[0];

		if (isPlace) {
			exprPlace = (Expression<String>) expressions[1];
			exprStart = (Expression<Date>) expressions[2];
			exprEnd = (Expression<Date>) expressions[3];
			exprGuild = (Expression<Guild>) expressions[4];
			exprResult = (Expression<Object>) expressions[5];
		} else {
			exprChannel = (Expression<GuildChannel>) expressions[1];
			exprDate = (Expression<Date>) expressions[2];
			exprResult = (Expression<Object>) expressions[3];
		}

		return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, ScheduledEvent.class);
	}

	@Override
	public void execute(@NotNull Event e) {
		final String name = parseSingle(exprName, e);
		if (anyNull(this, name))
			return;

		final ScheduledEvent event;
		if (isPlace) {
			final String place = parseSingle(exprPlace, e);
			final Date start = parseSingle(exprStart, e);
			final Date end = parseSingle(exprEnd, e);
			final Guild guild = parseSingle(exprGuild, e);
			if (anyNull(this, place, start, end, guild))
				return;

			try {
				event = guild
						.createScheduledEvent(name, place, SkriptUtils.convertDate(start), SkriptUtils.convertDate(end))
						.complete();
			} catch (Exception ex) {
				DiSkyRuntimeHandler.error((Exception) ex);
				return;
			}

		} else {
			final GuildChannel channel = parseSingle(exprChannel, e);
			final Date date = parseSingle(exprDate, e);
			if (channel == null || date == null)
				return;

			try {
				event = channel.getGuild().createScheduledEvent(name, channel, SkriptUtils.convertDate(date)).complete();
			} catch (Exception ex) {
				DiSkyRuntimeHandler.error((Exception) ex);
				return;
			}
		}

		exprResult.change(e, new ScheduledEvent[] {event}, Changer.ChangeMode.SET);
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		if (isPlace)
			return "create scheduled event named " + exprName.toString(e, debug) + " at " + exprPlace.toString(e, debug) + " starting at " + exprStart.toString(e, debug) + " and ending at " + exprEnd.toString(e, debug) + " in " + exprGuild.toString(e, debug);
		else return "create scheduled event named " + exprName.toString(e, debug) + " in " + exprChannel.toString(e, debug) + " at " + exprDate.toString(e, debug);
	}
}
