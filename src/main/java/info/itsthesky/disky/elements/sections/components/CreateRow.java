package info.itsthesky.disky.elements.sections.components;

import info.itsthesky.disky.api.skript.ReturningSection;
import info.itsthesky.disky.elements.components.core.ComponentRow;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateRow extends ReturningSection<ComponentRow> {

	public static class row extends LastBuilderExpression<ComponentRow, CreateRow> { }

	static {
		register(
				CreateRow.class,
				ComponentRow.class,
				row.class,
				"(make|create) [a] [new] [component[s]] row"
		);
	}

	@Override
	public ComponentRow createNewValue() {
		return new ComponentRow();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new components row";
	}

}
