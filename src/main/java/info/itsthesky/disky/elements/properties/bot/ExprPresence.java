package info.itsthesky.disky.elements.properties.bot;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.skript.NodeInformation;
import net.dv8tion.jda.api.entities.Activity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprPresence extends SimpleExpression<Activity> {

	static {
		Skript.registerExpression(ExprPresence.class, Activity.class, ExpressionType.SIMPLE,
				"listening [to] %string%",
				"watching [to] %string%",
				"playing [to] %string%",
				"streaming [to] %string% with [the] url %string%",
				"competing [to] %string%"
				);
	}

	private int pattern;
	private Expression<String> exprInput;
	private Expression<String> exprURL;
	private NodeInformation node;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		pattern = matchedPattern;
		exprInput = (Expression<String>) exprs[0];
		node = new NodeInformation();
		if (matchedPattern == 3) exprURL = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected Activity @NotNull [] get(@NotNull Event e) {
		String input = exprInput.getSingle(e);
		String url = exprURL == null ? null : exprURL.getSingle(e);
		if (input == null) return new Activity[0];
		Activity activity = null;
		switch (pattern) {
			case 0:
				activity = Activity.listening(input);
				break;
			case 1:
				activity = Activity.watching(input);
				break;
			case 2:
				activity = Activity.playing(input);
				break;
			case 3:
				if (url == null)
					DiSky.getErrorHandler().exception(e, "The streaming URL cannot be null or not set!");
				if (!Activity.isValidStreamingUrl(url))
					DiSky.getErrorHandler().exception(e, "The streaming URL specified for the presence is NOT valid! (Input: "+url+")");
				activity = Activity.streaming(input, url);
				break;
			case 4:
				activity = Activity.competing(input);
		}
		return new Activity[] {activity};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends Activity> getReturnType() {
		return Activity.class;
	}

	@Override
	public @NotNull String toString(Event e, boolean debug) {
		switch (pattern) {
			case 0:
				return "listening " + exprInput.toString(e, debug);
			case 1:
				return "watching " + exprInput.toString(e, debug);
			case 2:
				return "playing " + exprInput.toString(e, debug);
			case 3:
				return "streaming " + exprInput.toString(e, debug) + " with url " + exprURL.toString(e, debug);
			case 4:
				return "competing " + exprInput.toString(e, debug);
		}
		return "";
	}

}