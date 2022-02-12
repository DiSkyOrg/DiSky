package info.itsthesky.disky.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.WaiterEffect;
import info.itsthesky.disky.core.Bot;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Shutdown Bot")
@Description({"Stop and disconnect a loaded bot from DiSky & discord.",
		"However, if any requests was still remaining, they wil be executed before the actual bot shutdown."})
@Examples({"shutdown bot named \"name\"",
		"stop bot \"name\""})
public class StopBot extends WaiterEffect {

	static {
		Skript.registerEffect(
				StopBot.class,
				"(stop|shutdown) [the] [bot] %bot%"
		);
	}

	private Expression<Bot> exprBot;

	@Override
	public boolean initEffect(Expression[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		exprBot = (Expression<Bot>) expressions[0];
		return true;
	}

	@Override
	public void runEffect(Event e) {
		final Bot bot = parseSingle(exprBot, e, null);
		if (!anyNull(bot))
			bot.getInstance().shutdown();
		restart();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "shutdown bot " + exprBot.toString(e, debug);
	}
}
