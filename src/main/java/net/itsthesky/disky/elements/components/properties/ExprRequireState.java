package net.itsthesky.disky.elements.components.properties;

import net.dv8tion.jda.api.components.textinput.TextInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprRequireState extends SimpleChangeableProperty<Object, Boolean> {

	static {
		register(
				ExprRequireState.class,
				Boolean.class,
				"require[d] state",
				"textinput"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "required state";
	}

	@Override
	public @Nullable Boolean convert(Object entity) {
		if (entity instanceof TextInput.Builder)
			return ((TextInput.Builder) entity).isRequired();
		return null;
	}

	@Override
	public @NotNull Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	protected void set(@NotNull Object entity, @Nullable Boolean value) {
		if (entity instanceof TextInput.Builder)
			((TextInput.Builder) entity).setRequired(Boolean.TRUE.equals(value));
	}
}
