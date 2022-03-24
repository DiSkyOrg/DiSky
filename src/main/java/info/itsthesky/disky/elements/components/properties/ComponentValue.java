package info.itsthesky.disky.elements.components.properties;

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
import info.itsthesky.disky.elements.events.interactions.ModalSendEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Component Value")
@Description("Get the current value of a sent component, currently only working in modals with text input.")
@Examples("value of component with id \"XXX\"")
public class ComponentValue extends SimpleExpression<String> {

	static {
		Skript.registerExpression(
				ComponentValue.class,
				String.class,
				ExpressionType.COMBINED,
				"[the] [current] value of [the] (component|text[( |-)]input) [with [the] id] %string%"
		);
	}

	private Expression<String> exprId;

	@Override
	protected String @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(id))
			return new String[0];
		return new String[] {
				((ModalSendEvent.BukkitModalSendEvent) e).getJDAEvent().getValue(id).getAsString()
		};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "value of component with id " + exprId.toString(e, debug);
	}

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
		if (!EasyElement.containsEvent(ModalSendEvent.BukkitModalSendEvent.class)) {
			Skript.error("You can only get values of components in a modal receive event.");
			return false;
		}
		exprId = (Expression<String>) exprs[0];
		return true;
	}
}
