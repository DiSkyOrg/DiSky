package info.itsthesky.disky.elements.components.create;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.api.skript.EasyElement;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprNewModal extends SimpleExpression<Modal.Builder> {

	static {
		Skript.registerExpression(
				ExprNewModal.class,
				Modal.Builder.class,
				ExpressionType.COMBINED,
				"[a] [new] modal [with] [the] [id] %string% (named|with name) %string%"
		);
	}

	private Expression<String> exprId;
	private Expression<String> exprName;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull SkriptParser.ParseResult parseResult) {
		exprId = (Expression<String>) exprs[0];
		exprName = (Expression<String>) exprs[1];
		return true;
	}

	@Override
	protected Modal.Builder @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		final String name = EasyElement.parseSingle(exprName, e, null);
		if (EasyElement.anyNull(id, name))
			return new Modal.Builder[0];
		return new Modal.Builder[] {Modal.create(id, name)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends Modal.Builder> getReturnType() {
		return Modal.Builder.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "new modal with id " + exprId.toString(e, debug) + " named " + exprName.toString(e, debug);
	}

}
