package info.itsthesky.disky.elements.components.properties;

import net.dv8tion.jda.api.interactions.components.text.TextInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprValue extends SimpleChangeableProperty<Object, String> {

	static {
		register(
				ExprValue.class,
				String.class,
				"[default] value",
				"textinput"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "max range";
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof TextInput.Builder)
			return ((TextInput.Builder) entity).getValue();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected void set(@NotNull Object entity, @Nullable String value) {
		if (entity instanceof TextInput.Builder)
			((TextInput.Builder) entity).setValue(value);
	}
}
