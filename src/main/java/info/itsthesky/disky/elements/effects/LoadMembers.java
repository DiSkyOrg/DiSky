package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Variable;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.SpecificBotEffect;
import info.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Load Members")
@Description({"Load every members of a guild.",
"This effect will also cache members that were not, so execution may be delayed.",
"consider calling this effect once, then use the default member expression to get the members."})
@Examples("load members of event-guild and store them in {_members::*}")
@Since("4.0.0")
public class LoadMembers extends SpecificBotEffect<List<Member>> {

	static {
		Skript.registerEffect(
				LoadMembers.class,
				"load [all] members (of|from) [the] %guild% and store (them|the members) in %-objects%"
		);
	}

	private Expression<Guild> exprGuild;

	@Override
	public boolean initEffect(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprGuild = (Expression<Guild>) expressions[0];
		setChangedVariable((Variable<List<Member>>) expressions[1]);
		return true;
	}

	@Override
	public void runEffect(@NotNull Event e, Bot bot) {
		final Guild initialGuild = parseSingle(exprGuild, e, null);
		if (initialGuild == null) {
			restart();
			return;
		}

		final Guild guild = bot == null || bot.coreIsEquals(initialGuild.getJDA()) ? initialGuild : bot.getInstance().getGuildById(initialGuild.getId());
		if (guild == null) {
			restart();
			return;
		}

		guild.loadMembers().onSuccess(this::restart)
				.onError(ex -> {
					restart();
					DiSky.getErrorHandler().exception(event, ex);
				});
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "load members of "+  exprGuild.toString(e, debug) + " and store them in " + getChangedVariable().toString(e, debug);
	}
}
