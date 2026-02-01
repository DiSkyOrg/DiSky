package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("New Option Choice")
@Description({"Create a new slash command option choice with an unique name and a string or number value.",
"Choices are only available for STRING, NUMBER and INTEGER slash command option type.",
"Of course, the provided value type must be compatible with the option type (you cannot add string choice to a NUMBER option)."})
@Examples("add new choice named \"Example choice\" with value 100 to choices of {_option} # it's a NUMBER option")
public class ExprNewOptionChoice extends SimpleExpression<Command.Choice> {

	static {
		DiSkyRegistry.registerExpression(
				ExprNewOptionChoice.class,
				Command.Choice.class,
				ExpressionType.COMBINED,
				"[a] new [option] choice [(named|with name)] %string% with [the] value %string/number%"
		);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		exprValue = (Expression<Object>) exprs[1];
		return true;
	}

	private Expression<String> exprName;
	private Expression<Object> exprValue;

	@Override
	protected Command.Choice @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e, null);
		final Object value = EasyElement.parseSingle(exprValue, e, null);
		if (EasyElement.anyNull(this, name, value))
			return new Command.Choice[0];
		final Command.Choice choice;
		if (value instanceof String)
			choice = new Command.Choice(name, (String) value);
		else {
			if (((Number) value).doubleValue() % 1 == 0)
				choice = new Command.Choice(name, ((Number) value).intValue());
			else
				choice = new Command.Choice(name, ((Number) value).doubleValue());
		}
		return new Command.Choice[] {choice};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends Command.Choice> getReturnType() {
		return Command.Choice.class;
	}

	@Override
	public @NotNull String toString(Event e, boolean debug) {
		return "new choice named " + exprName.toString(e, debug) + " with value " + exprValue.toString(e, debug);
	}
}
