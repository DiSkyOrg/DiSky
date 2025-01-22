package net.itsthesky.disky.api.skript.reflects;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import net.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReflectMultipleProperty extends MultiplyPropertyExpression<Object, Object> {

	@Override
	protected @NotNull String getPropertyName() {
		throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
	}

	@Override
	public @Nullable Object[] convert(Object entry) {
		throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
	}

	@Override
	public @NotNull Class<?> getReturnType() {
		return Object.class;
	}
}
