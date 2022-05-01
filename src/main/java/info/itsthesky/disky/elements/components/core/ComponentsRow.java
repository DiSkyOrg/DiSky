package info.itsthesky.disky.elements.components.core;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.EasyElement;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ComponentsRow extends MultiplyPropertyExpression<Object, ComponentRow> {

	static {
		register(
				ComponentsRow.class,
				ComponentRow.class,
				"component[s] row[s]",
				"modal/message"
		);
	}

	@Override
	public @NotNull Class<? extends ComponentRow> getReturnType() {
		return ComponentRow.class;
	}

	@Override
	protected String getPropertyName() {
		return "components row";
	}

	@Override
	protected ComponentRow[] convert(Object entity) {
		if (entity instanceof Modal.Builder)
			return ComponentRow.from(((Modal.Builder) entity).getActionRows()).toArray(new ComponentRow[0]);
		if (entity instanceof Message)
			return ComponentRow.from(((Message) entity).getActionRows()).toArray(new ComponentRow[0]);
		return new ComponentRow[0];
	}

	@Override
	public Class<?> @NotNull [] acceptChange(Changer.@NotNull ChangeMode mode) {
		if (EasyElement.equalAny(mode,
				Changer.ChangeMode.SET,
				Changer.ChangeMode.ADD,
				Changer.ChangeMode.REMOVE_ALL,
				Changer.ChangeMode.RESET))
			return new Class[] {ComponentRow[].class, ComponentRow.class};
		return new Class[0];
	}

	@Override
	public void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
		final Object entity = getExpr().getSingle(e);
		if (delta == null)
			return;
		final List<ActionRow> rows = Arrays
				.stream((ComponentRow[]) delta)
				.map(ComponentRow::asActionRow)
				.collect(Collectors.toList());

		if (entity instanceof Message) {
			final List<ActionRow> currents = new ArrayList<>(((Message) entity).getActionRows());
			switch (mode) {
				case ADD:
					currents.addAll(rows);
					break;
				case REMOVE_ALL:
				case RESET:
					currents.clear();
					break;
				case SET:
					currents.clear();
					currents.addAll(rows);
					break;
			}
			((Message) entity).editMessage((Message) entity).setActionRows(currents).queue();
		} else if (entity instanceof Modal.Builder) {
			final List<ActionRow> currents = ((Modal.Builder) entity).getActionRows();
			switch (mode) {
				case ADD:
					currents.addAll(rows);
					break;
				case REMOVE_ALL:
				case RESET:
					currents.clear();
					break;
				case SET:
					currents.clear();
					currents.addAll(rows);
					break;
			}
		}
	}
}
