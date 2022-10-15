package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewDropdown extends SimpleExpression<SelectMenu.Builder> {

	static {
		Skript.registerExpression(
				ExprNewDropdown.class,
				SelectMenu.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] drop[( |-)]down [with] [the] [id] %string%"
		);
	}

	private Expression<String> exprId;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprId = (Expression<String>) exprs[0];
		return true;
	}

	@Override
	protected SelectMenu.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(id))
			return new SelectMenu.Builder[0];
		return new SelectMenu.Builder[] {SelectMenu.create(id)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends SelectMenu.Builder> getReturnType() {
		return SelectMenu.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new dropdown with id " + exprId.toString(e, debug);
	}

}
