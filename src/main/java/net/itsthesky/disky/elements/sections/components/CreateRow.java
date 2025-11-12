package net.itsthesky.disky.elements.sections.components;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import net.itsthesky.disky.api.skript.ReturningSection;
import net.itsthesky.disky.elements.components.core.ComponentRow;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Creator Components Row")
@Description({"Creates a row of components.",
		"The specified variable will contains the created row once the section is executed.",
		"For now, a row can only hold multiple components of type Button!",
		"Use 'the last row builder' expression (within the section) to change the values of the row!"
})
@Examples("create a new row and store it in {_row}:\n" +
		"        \n" +
		"    add new danger button with id \"test\" named \"Hello World\" with reaction \"smile\" to the components of the row\n" +
		"    add new success button with id \"test2\" named \"yuss\" to the components of the row")
@Since("4.0.0")
public class CreateRow extends ReturningSection<ComponentRow> {

	@Name("Last Row Builder")
	@Description("Represents the last row builder created within a section.")
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
	public ComponentRow createNewValue(Event event) {
		return new ComponentRow();
	}

	@Override
	public @NotNull String toString(@Nullable Event e, boolean debug) {
		return "create a new components row";
	}

}
