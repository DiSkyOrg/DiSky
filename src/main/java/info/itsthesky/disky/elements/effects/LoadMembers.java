package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.AsyncEffect;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static info.itsthesky.disky.api.skript.EasyElement.parseSingle;

@Name("Load Members")
@Description({"Load every members of a guild.",
"This effect will also cache members that were not, so execution may be delayed.",
"consider calling this effect once, then use the default member expression to get the members."})
@Examples("load members of event-guild and store them in {_members::*}")
@Since("4.0.0")
public class LoadMembers extends AsyncEffect {

	static {
		Skript.registerEffect(
				LoadMembers.class,
				"load [all] members (of|from) [the] %guild% and store (them|the members) in %~objects%"
		);
	}

	private Expression<Guild> exprGuild;
	private Expression<Object> exprResult;

	@Override
	public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, ParseResult parseResult) {
		getParser().setHasDelayBefore(Kleenean.TRUE);

		exprGuild = (Expression<Guild>) expressions[0];
		exprResult = (Expression<Object>) expressions[1];

		return Changer.ChangerUtils.acceptsChange(exprResult, Changer.ChangeMode.SET, Member[].class);
	}

	@Override
	public void execute(@NotNull Event e) {
		final Guild guild = parseSingle(exprGuild, e, null);
		if (guild == null)
			return;

		final List<Member> members;
		try {
			members = guild.loadMembers().get();
		} catch (Exception ex) {
			DiSky.getErrorHandler().exception(e, ex);
			return;
		}

		exprResult.change(e, members.toArray(new Member[0]), Changer.ChangeMode.SET);
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "load members of "+  exprGuild.toString(e, debug)
				+ " and store them in " + exprResult.toString(e, debug);
	}
}
