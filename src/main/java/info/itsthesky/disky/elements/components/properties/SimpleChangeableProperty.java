package info.itsthesky.disky.elements.components.properties;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.skript.EasyElement;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleChangeableProperty<F, T> extends SimplePropertyExpression<F, T> {

	protected abstract void set(@NotNull F entity, @Nullable T value);

	@Override
	public void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
		final F entity = EasyElement.parseSingle(getExpr(), e, null);
		final T value = (T) delta[0];

		if (EasyElement.anyNull(entity, value))
			return;
		if (!EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET))
			return;

		set(entity, mode == Changer.ChangeMode.SET ? value : null);
	}

	@Override
	public Class<?> @NotNull [] acceptChange(@NotNull Changer.ChangeMode mode) {
		if (EasyElement.equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.RESET))
			return new Class[] {getReturnType()};
		return new Class[0];
	}
}
