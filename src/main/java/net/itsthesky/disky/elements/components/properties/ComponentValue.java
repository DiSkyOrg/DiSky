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
import net.dv8tion.jda.api.entities.Message;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.elements.events.rework.ComponentEvents;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Modal Component Value / Values")
@Description({"Get the current value(s) of a sent component, currently only working in modals with text input, select menus, and attachment uploads.",
		"You have to precise what type of component you are trying to get: 'textinput', 'dropdown', or 'attachment'."})
@Examples({"values of dropdown with id \"XXX\"",
		"value of textinput with id \"XXX\"",
		"attachments of attachment with id \"user_avatar\""})
public class ComponentValue extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(
				ComponentValue.class,
				Object.class,
				ExpressionType.COMBINED,
				"[the] [current] value[s] of [the] (1¦text[( |-)]input|2¦drop[( |-)]down) [with [the] id] %string%",
				"[the] [current] attachment[s] of [the] (3¦attachment[( |-)]upload) [with [the] id] %string%"
		);
	}

	private Expression<String> exprId;

	@Override
	protected Object @NotNull [] get(@NotNull Event e) {
		final String id = EasyElement.parseSingle(exprId, e, null);
		if (EasyElement.anyNull(this, id))
			return new Object[0];
		final var event = ComponentEvents.MODAL_INTERACTION_EVENT.getJDAEvent(e);
		if (event == null)
			return new Object[0];

		final var mapping = event.getValue(id);
		if (mapping == null)
			return new Object[0];

		// Handle attachment uploads (mark 3)
		if (returnType == Message.Attachment.class) {
			final var attachments = mapping.getAsAttachmentList();
			return attachments.toArray(new Message.Attachment[0]);
		}

		// Handle text input (mark 1) - single value
		if (isSingle())
			return new String[] { mapping.getAsString() };

		// Handle dropdown (mark 2) - multiple values
		return mapping.getAsStringList().toArray(new String[0]);
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
		if (!EasyElement.containsEvent(ComponentEvents.MODAL_INTERACTION_EVENT.getBukkitEventClass())) {
			Skript.error("You can only get values of components in a modal receive event.");
			return false;
		}

		// mark 1 = textinput (single String)
		// mark 2 = dropdown (multiple Strings)
		// mark 3 = attachment upload (multiple Attachments)
		if (parseResult.mark == 3) {
			returnType = Message.Attachment.class;
			isSingle = false;
		} else {
			returnType = parseResult.mark == 1 ? String.class : String.class;
			isSingle = parseResult.mark == 1;
		}

		exprId = (Expression<String>) exprs[0];
		return true;
	}
}
