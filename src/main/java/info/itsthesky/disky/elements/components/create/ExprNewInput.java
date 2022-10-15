package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
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
				"[a] [new] text[( |-)]input [with] [the] [id] %string% (named|with name) %string%",
				"[a] [new] short text[( |-)]input [with] [the] [id] %string% (named|with name) %string%"
		);
	}

	private Expression<String> exprId;
	private Expression<String> exprName;
	private TextInputStyle style;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprId = (Expression<String>) exprs[0];
		exprName = (Expression<String>) exprs[1];
		style = matchedPattern == 0 ? TextInputStyle.PARAGRAPH : TextInputStyle.SHORT;
		return true;
	}

	@Override
	protected TextInput.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		final String name = EasyElement.parseSingle(exprName, e, null);
		if (EasyElement.anyNull(id, name))
			return new TextInput.Builder[0];
		return new TextInput.Builder[] {TextInput.create(id, name, style)};
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
		return "new modal with id " + exprId.toString(e, debug) + " named " + exprName.toString(e, debug);
	}

}
