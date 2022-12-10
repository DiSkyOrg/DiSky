package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Date;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Create Scheduled Event")
@Description({"Create a new scheduled event in a specific channel or at a specific place.",
"If you use the second pattern (without channel), you'll have to specify the guild where the event will be created, and a start & end date.",
"If you use the first pattern (with channel), you'll just have to specific the channel itself and a start date.",
"Scheduled events currently only support stage & voice channels."})
@Examples({
		"create scheduled event named \"Let's Talk Together\" in stage channel with id \"000\" at (5 hours after now) and store it in {_event}",
		"create scheduled event named \"Concerto\" at \"6 routes of XXX\" starting (1 hour after now) and ending (5 hours after now) in event-guild and store it in {_event}"
})
public class CreateScheduledEvent extends SpecificBotEffect<ScheduledEvent> {

	static {
		Skript.registerEffect(
				CreateScheduledEvent.class,
				"create [a] [new] scheduled event (with name|named) %string% in %guildchannel% at %date% and store (it|the event) in %objects%",
				"create [a] [new] scheduled event (with name|named) %string% at %string% starting [at] %date% [and] ending [at] %date% in %guild% and store (it|the event) in %objects%"
		);
	}

	private Expression<String> exprName;

	private Expression<GuildChannel> exprChannel;
	private Expression<Date> exprDate;

	private Expression<String> exprPlace;
	private Expression<Date> exprStart;
	private Expression<Date> exprEnd;
	private Expression<Guild> exprGuild;

	private boolean isPlace = false;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		isPlace = i == 1;
		exprName = (Expression<String>) expressions[0];

		if (isPlace) {
			exprPlace = (Expression<String>) expressions[1];
			exprStart = (Expression<Date>) expressions[2];
			exprEnd = (Expression<Date>) expressions[3];
			exprGuild = (Expression<Guild>) expressions[4];
			return validateVariable(expressions[5], false, true);
		} else {
			exprChannel = (Expression<GuildChannel>) expressions[1];
			exprDate = (Expression<Date>) expressions[2];
			return validateVariable(expressions[3], false, true);
		}
	}

	@Override
	public void runEffect(@NotNull Event e, @NotNull Bot bot) {
		final String name = parseSingle(exprName, e);
		if (name == null) {
			restart();
			return;
		}

		if (isPlace) {
			final String place = parseSingle(exprPlace, e);
			final Date start = parseSingle(exprStart, e);
			final Date end = parseSingle(exprEnd, e);
			final Guild guild = parseSingle(exprGuild, e);
			if (place == null || start == null || end == null || guild == null) {
				restart();
				return;
			}
			guild.createScheduledEvent(name, place, SkriptUtils.convertDate(start), SkriptUtils.convertDate(end)).queue(this::restart, ex -> exception(event, ex));
		} else {
			final GuildChannel channel = parseSingle(exprChannel, e);
			final Date date = parseSingle(exprDate, e);
			if (channel == null || date == null) {
				restart();
				return;
			}
			channel.getGuild().createScheduledEvent(name, channel, SkriptUtils.convertDate(date)).queue(event -> restart(), ex -> exception(event, ex));
		}
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		if (isPlace)
			return "create scheduled event named " + exprName.toString(e, debug) + " at " + exprPlace.toString(e, debug) + " starting at " + exprStart.toString(e, debug) + " and ending at " + exprEnd.toString(e, debug) + " in " + exprGuild.toString(e, debug);
		else return "create scheduled event named " + exprName.toString(e, debug) + " in " + exprChannel.toString(e, debug) + " at " + exprDate.toString(e, debug);
	}
}
