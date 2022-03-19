package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewSlashCommand extends SimpleExpression<SlashCommandData> {

	static {
		Skript.registerExpression(
				ExprNewSlashCommand.class,
				SlashCommandData.class,
				ExpressionType.COMBINED,
				"[a] [new] slash[( |-)]command [with] [(the name|named)] %string% [and] with [the] desc[ription] %string%"
		);
	}

	private Expression<String> exprName;
	private Expression<String> exprDesc;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		exprName = (Expression<String>) exprs[0];
		exprDesc = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected SlashCommandData @NotNull [] get(@NotNull Event e) {
		final String name = EasyElement.parseSingle(exprName, e, null);
		final String desc = EasyElement.parseSingle(exprDesc, e, null);
		if (EasyElement.anyNull(name, desc))
			return new SlashCommandData[0];
		return new SlashCommandData[] {Commands.slash(name, desc)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends SlashCommandData> getReturnType() {
		return SlashCommandData.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new slash command named " + exprName.toString(e, debug) + " with description " + exprDesc.toString(e, debug);
	}

}
