package info.itsthesky.disky.elements.components.commands;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewSlashOption extends SimpleExpression<OptionData> {

	static {
		Skript.registerExpression(
				ExprNewSlashOption.class,
				OptionData.class,
				ExpressionType.COMBINED,
				"[a] [new] [slash[( |-)]command] [(:required)] [(:auto[( |-)]complete)] (%-optiontype%|:member) option [(named|with name)] %string% with [the] desc[ription] %string%"
		);
	}

	private Expression<OptionType> exprType;
	private Expression<String> exprName;
	private Expression<String> exprDesc;
	private boolean required, autoComplete, member;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		exprType = (Expression<OptionType>) exprs[0];
		exprName = (Expression<String>) exprs[1];
		exprDesc = (Expression<String>) exprs[2];

		member = parseResult.hasTag("member");
		required = parseResult.hasTag("required");
		autoComplete = parseResult.hasTag("auto");

		return true;
	}

	@Override
	protected OptionData @NotNull [] get(@NotNull Event e) {
		final OptionType type = EasyElement.parseSingle(exprType, e, null);
		final String name = EasyElement.parseSingle(exprName, e, null);
		final String desc = EasyElement.parseSingle(exprDesc, e, null);
		if (EasyElement.anyNull(this, name, desc))
			return new OptionData[0];

		if (member) {
			return new OptionData[] {new OptionData(OptionType.USER, name, desc, required, autoComplete)};
		} else {
			if (type == null)
				return new OptionData[0];
			return new OptionData[] {new OptionData(type, name, desc, required, autoComplete)};
		}
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends OptionData> getReturnType() {
		return OptionData.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new "+exprType.toString(e, debug) +" option named " + exprName.toString(e, debug) + " with description " + exprDesc.toString(e, debug);
	}

}
