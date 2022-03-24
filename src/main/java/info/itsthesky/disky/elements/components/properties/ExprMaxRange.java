package info.itsthesky.disky.elements.components.properties;

import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprMaxRange extends SimpleChangeableProperty<Object, Number> {

	static {
		register(
				ExprMaxRange.class,
				Number.class,
				"max[imum] range",
				"dropdown/textinput"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "max range";
	}

	@Override
	public @Nullable Number convert(Object entity) {
		if (entity instanceof SelectMenu.Builder)
			return ((SelectMenu.Builder) entity).getMaxValues();
		if (entity instanceof TextInput.Builder)
			return ((TextInput.Builder) entity).getMaxLength();
		return null;
	}

	@Override
	public @NotNull Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	protected void set(@NotNull Object entity, @Nullable Number value) {
		if (entity instanceof SelectMenu.Builder)
			((SelectMenu.Builder) entity).setMaxValues(value == null ? 1 : value.intValue());
		if (entity instanceof TextInput.Builder)
			((TextInput.Builder) entity).setMaxLength(value == null ? 0 : value.intValue());
	}
}
