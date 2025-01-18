package net.itsthesky.disky.elements.components.properties;

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
import net.itsthesky.disky.elements.events.interactions.ModalSendEvent;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Modal Component Value / Values")
@Description({"Get the current value(s) of a sent component, currently only working in modals with text input & select menus.",
		"You have to precise what type of component you are trying to get, either 'textinput' or 'dropdown'."})
@Examples({"values of dropdown with id \"XXX\"",
		"value of textinput with id \"XXX\""})
public class ComponentValue extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(
				ComponentValue.class,
				Object.class,
				ExpressionType.COMBINED,
				"[the] [current] value[s] of [the] (1¦text[( |-)]input|2¦drop[( |-)]down) [with [the] id] %string%"
		);
	}

	private Expression<String> exprId;

	@Override
	protected Object @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(this, id))
			return new String[0];
		final ModalMapping mapping = ((ModalSendEvent.BukkitModalSendEvent) e).getJDAEvent().getValue(id);
		if (mapping == null)
			return new String[0];

		if (isSingle())
			return new String[] { mapping.getAsString() };
		else
			return new Object[0];
			//return mapping.getAsStringList().toArray(new String[0]);

	}

	@Override
	public boolean isSingle() {
		return isSingle;
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "value of component with id " + exprId.toString(e, debug);
	}

	private Class<?> returnType;
	private boolean isSingle;

	@Override
	public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult parseResult) {
		if (!EasyElement.containsEvent(ModalSendEvent.BukkitModalSendEvent.class)) {
			Skript.error("You can only get values of components in a modal receive event.");
			return false;
		}
		returnType = parseResult.mark == 1 ? String.class : String[].class;
		isSingle = parseResult.mark == 1;
		exprId = (Expression<String>) exprs[0];
		return true;
	}
}
