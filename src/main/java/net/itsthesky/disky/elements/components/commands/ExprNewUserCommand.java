package net.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New User Command")
@Description({"Create a new user command, to be updated on discord later.",
"This will create a context command of USER type.",
"Once created, you can execute it by right-clicking on a user, then going in 'Applications' -> <your user command name>"})
@Examples("new user command named \"Warn User\"")
public class ExprNewUserCommand extends SimpleExpression<CommandData> {

	static {
		Skript.registerExpression(
				ExprNewUserCommand.class,
				CommandData.class,
				ExpressionType.COMBINED,
				"[a] [new] user[( |-)]command [with] [(the name|named)] %string%"
		);
	}

	private Expression<String> exprName;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected CommandData @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e, null);
		if (EasyElement.anyNull(this, name))
			return new CommandData[0];
		return new CommandData[] {Commands.user(name)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends CommandData> getReturnType() {
		return CommandData.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new user command named " + exprName.toString(e, debug);
	}

}
