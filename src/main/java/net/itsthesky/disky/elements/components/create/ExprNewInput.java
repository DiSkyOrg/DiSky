package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewInput extends SimpleExpression<TextInput.Builder> {

	static {
		Skript.registerExpression(
				ExprNewInput.class,
				TextInput.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] [(:required)] text[( |-)]input [with] [the] [id] %string% (named|with name) %string% [with [the] value %-string%]",
				"[a] [new] [(:required)] short text[( |-)]input [with] [the] [id] %string% (named|with name) %string% [with [the] value %-string%]"
		);
	}

	private boolean required;
	private Expression<String> exprId;
	private Expression<String> exprName;
	private TextInputStyle style;
	private Expression<String> exprValue;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		required = parseResult.hasTag("required");
		exprId = (Expression<String>) exprs[0];
		exprName = (Expression<String>) exprs[1];
		style = matchedPattern == 0 ? TextInputStyle.PARAGRAPH : TextInputStyle.SHORT;
		exprValue = (Expression<String>) exprs[2];
		return true;
	}

	@Override
	protected TextInput.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		final String name = EasyElement.parseSingle(exprName, e, null);
		final @Nullable String value = EasyElement.parseSingle(exprValue, e, null);
		if (EasyElement.anyNull(this, id, name))
			return new TextInput.Builder[0];
		final var input = TextInput.create(id, name, style)
				.setRequired(required);
		if (value != null)
			input.setValue(value);

		return new TextInput.Builder[] {input};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends TextInput.Builder> getReturnType() {
		return TextInput.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new " + (required ? "required " : "") + style.name().toLowerCase()
				+ " text input with id " + exprId.toString(e, debug)
				+ " named " + exprName.toString(e, debug)
				+ (exprValue == null ? "" : " with value " + exprValue.toString(e, debug));
	}

}
