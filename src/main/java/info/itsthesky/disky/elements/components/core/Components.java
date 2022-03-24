package info.itsthesky.disky.elements.components.core;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Components extends MultiplyPropertyExpression<ComponentRow, Component> {

	static {
		register(
				Components.class,
				Component.class,
				"component[s]",
				"row"
		);
	}

	@Override
	public @NotNull Class<? extends Component> getReturnType() {
		return Component.class;
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		final ComponentRow row = getExpr().getSingle(e);
		if (row == null)
			return;
		final List<Component> components = new ArrayList<>();

		for (Object entity : delta) {
			if (entity instanceof SelectMenu.Builder)
				components.add(((SelectMenu.Builder) entity).build());
			else if (entity instanceof TextInput.Builder)
				components.add(((TextInput.Builder) entity).build());
			else
				components.add((Component) entity);
		}

		row.addAll(components);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (EasyElement.equalAny(mode,
				Changer.ChangeMode.SET,
				Changer.ChangeMode.ADD,
				Changer.ChangeMode.REMOVE_ALL,
				Changer.ChangeMode.RESET))
			return new Class[] {Button.class, SelectMenu.Builder.class,
					TextInput.Builder.class};
		return new Class[0];
	}

	@Override
	protected String getPropertyName() {
		return "components";
	}

	@Override
	protected Component[] convert(ComponentRow row) {
		return row.asComponents().toArray(new ItemComponent[0]);
	}
}
