package net.itsthesky.disky.api.skript.reflects;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReflectChangeableProperty extends SimplePropertyExpression<Object, Object> {

    @Override
    protected @NotNull String getPropertyName() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public @Nullable Object convert(Object entry) {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }
}