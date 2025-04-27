package net.itsthesky.disky.elements.components.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public abstract class SimpleChangeableProperty<F, T> extends SimplePropertyExpression<F, T> {

	protected abstract void set(@NotNull F entity, @Nullable T values);

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		final var entities = EasyElement.parseList(getExpr(), e, null);
		final T value = (T) delta[0];

		if (EasyElement.anyNull(this, value))
			return;
		if (!EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET))
			return;

		for (final var entity : entities) {
			if (entity != null)
				set(entity, mode == Changer.ChangeMode.SET ? value : null);
		}
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET))
			return new Class[] {getReturnType()};
		return new Class[0];
	}
}
