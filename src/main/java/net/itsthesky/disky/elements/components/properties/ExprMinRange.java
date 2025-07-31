package net.itsthesky.disky.elements.components.properties;

import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMinRange extends SimpleChangeableProperty<Object, Number> {

	static {
		register(
				ExprMinRange.class,
				Number.class,
				"min[imum] range",
				"dropdown/textinput"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "min range";
	}

	@Override
	public @Nullable Number convert(Object entity) {
		if (entity instanceof SelectMenu.Builder)
			return ((SelectMenu.Builder) entity).getMinValues();
		if (entity instanceof TextInput.Builder)
			return ((TextInput.Builder) entity).getMinLength();
		return null;
	}

	@Override
	public @NotNull Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected void set(@NotNull Object entity, @Nullable Number value) {
		if (entity instanceof SelectMenu.Builder)
			((SelectMenu.Builder) entity).setMinValues(value == null ? 1 : value.intValue());
		if (entity instanceof TextInput.Builder)
			((TextInput.Builder) entity).setMinLength(value == null ? 0 : value.intValue());
	}
}
