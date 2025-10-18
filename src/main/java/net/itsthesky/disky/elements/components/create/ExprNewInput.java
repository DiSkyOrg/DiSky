package net.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewInput extends SimpleExpression<TextInput.Builder> {

	static {
		Skript.registerExpression(
				ExprNewInput.class,
                TextInput.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] [(:required)] [(:short)] text[( |-)]input [with] [the] [id] %string% [with [the] value %-string%]"
		);
	}

	private boolean required;
	private Expression<String> exprId;
	private TextInputStyle style;
	private Expression<String> exprValue;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		required = parseResult.hasTag("required");
        style = parseResult.hasTag("short") ? TextInputStyle.SHORT : TextInputStyle.PARAGRAPH;

		exprId = (Expression<String>) exprs[0];
		exprValue = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected TextInput.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		final @Nullable String value = EasyElement.parseSingle(exprValue, e, null);
		if (EasyElement.anyNull(this, id))
			return new TextInput.Builder[0];

        final var input = TextInput.create(id, style)
                .setRequired(required)
                .setValue(value);
		return new TextInput.Builder[] {input};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<TextInput.Builder> getReturnType() {
		return TextInput.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new " + (required ? "required " : "") + style.name().toLowerCase()
				+ " text input with id " + exprId.toString(e, debug)
				+ (exprValue == null ? "" : " with value " + exprValue.toString(e, debug));
	}

}
