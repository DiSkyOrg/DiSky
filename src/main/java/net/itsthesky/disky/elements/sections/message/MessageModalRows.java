package net.itsthesky.disky.elements.sections.message;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.components.ActionComponent;
import net.dv8tion.jda.api.components.Component;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Name("Message Builder Component Rows")
@Description({"Component rows of a message builder",
		"See also: 'Creator Components Row'"})
public class MessageModalRows extends MultiplyPropertyExpression<Object, Object> {

	static {
		register(
				MessageModalRows.class,
				Object.class,
				"[component[s]] row[s]",
				"messagecreatebuilder/modal"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.ADD)
			return new Class[]{
					ComponentRow.class, ComponentRow[].class,
					Button.class, Button[].class,
					SelectMenu.Builder.class, SelectMenu.Builder[].class,
					TextInput.Builder.class, TextInput.Builder[].class,
					Modal.Builder.class, Modal.Builder[].class
		};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final Object builder = EasyElement.parseSingle(getExpr(), e, null);
		final List<ActionRow> rows = new LinkedList<>();

		if (builder == null)
			return;

		for (Object obj : delta) {
			if (obj instanceof Button)
				rows.add(ActionRow.of((Button) obj));
			else if (obj instanceof ComponentRow)
				rows.add(((ComponentRow) obj).asActionRow());
			else if (obj instanceof SelectMenu.Builder)
				rows.add(ActionRow.of(((SelectMenu.Builder) obj).build()));
			else if (obj instanceof TextInput.Builder)
				rows.add(ActionRow.of(((TextInput.Builder) obj).build()));
		}

		if (builder instanceof MessageCreateBuilder)
			((MessageCreateBuilder) builder).addComponents(rows);
		else if (builder instanceof Modal.Builder)
			((Modal.Builder) builder).addComponents(rows);
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}

	@Override
	protected String getPropertyName() {
		return "rows";
	}

	@Override
	protected Object[] convert(Object object) {
		if (object instanceof MessageCreateBuilder)
			return ((MessageCreateBuilder) object).getComponents().toArray(new Component[0]);
		else if (object instanceof Modal.Builder)
			return ((Modal.Builder) object).getComponents().toArray(new Component[0]);

		return new Object[0];
	}
}
