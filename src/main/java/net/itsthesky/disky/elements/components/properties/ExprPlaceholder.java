package net.itsthesky.disky.elements.components.properties;

import net.dv8tion.jda.api.components.ModalTopLevelComponent;
import net.dv8tion.jda.api.components.selections.SelectMenu;
import net.dv8tion.jda.api.components.textinput.TextInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprPlaceholder extends SimpleChangeableProperty<Object, String> {

	static {
		register(
				ExprPlaceholder.class,
				String.class,
				"[discord] place[( |-)]holder",
				"object"
		);
	}

	@Override
	protected @NotNull String getPropertyName() {
		return "placeholder";
	}

	@Override
	public @Nullable String convert(Object entity) {
		if (entity instanceof SelectMenu.Builder)
			return ((SelectMenu.Builder<?, ?>) entity).getPlaceholder();
		if (entity instanceof ModalTopLevelComponent)
			return ((TextInput.Builder) entity).getPlaceholder();
		return null;
	}

	@Override
	public @NotNull Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	protected void set(@NotNull Object entity, @Nullable String value) {
		if (entity instanceof SelectMenu.Builder)
			((SelectMenu.Builder<?, ?>) entity).setPlaceholder(value);
		if (entity instanceof TextInput.Builder)
			((TextInput.Builder) entity).setPlaceholder(value);
	}
}
