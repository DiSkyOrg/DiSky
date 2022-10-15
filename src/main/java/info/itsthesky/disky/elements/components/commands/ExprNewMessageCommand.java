package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("New Message Command")
@Description({"Create a new message command, to be updated on discord later.",
"This will create a context command of MESSAGE type.",
"Once created, you can execute it by right-clicking on a message, then going in 'Applications' -> <your message command name>"})
@Examples("new message command named \"Warn Message\"")
public class ExprNewMessageCommand extends SimpleExpression<CommandData> {

	static {
		Skript.registerExpression(
				ExprNewMessageCommand.class,
				CommandData.class,
				ExpressionType.COMBINED,
				"[a] [new] message[( |-)]command [with] [(the name|named)] %string%"
		);
	}

	private Expression<String> exprName;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected CommandData @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e, null);
		if (EasyElement.anyNull(name))
			return new CommandData[0];
		return new CommandData[] {Commands.message(name)};
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
		return "new message command named " + exprName.toString(e, debug);
	}

}
