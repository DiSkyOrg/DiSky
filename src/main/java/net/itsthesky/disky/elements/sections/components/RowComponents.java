package net.itsthesky.disky.elements.sections.components;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import net.dv8tion.jda.api.components.ActionComponent;
import net.itsthesky.disky.api.skript.EasyElement;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Row Builder Components")
@Description({"Components of a row builder",
		"See also: 'Create (rich) Message'"})
public class RowComponents extends MultiplyPropertyExpression<ComponentRow, Object> {

	static {
		register(
				RowComponents.class,
				Object.class,
				"component[s]",
				"row"
		);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.ADD))
			return new Class[]{
					SelectMenu.Builder.class,
					Button.class, Button[].class,
					TextInput.Builder.class, TextInput.Builder[].class
		};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		if (!EasyElement.isValid(delta))
			return;

		final ComponentRow row = EasyElement.parseSingle(getExpr(), e);
		if (row == null)
			return;

		for (Object comp : delta) {
			if (comp instanceof SelectMenu.Builder)
				row.setMenu(((SelectMenu.Builder<?, ?>) comp).build());
			else if (comp instanceof Button)
				row.add((Button) comp);
			else if (comp instanceof TextInput.Builder)
				row.setInput(((TextInput.Builder) comp).build());
		}
	}

	@Override
	public @NotNull Class<? extends ActionComponent> getReturnType() {
		return ActionComponent.class;
	}

	@Override
	protected String getPropertyName() {
		return "components";
	}

	@Override
	protected ActionComponent[] convert(ComponentRow row) {
		return row.asComponents().toArray(new ActionComponent[0]);
	}
}
