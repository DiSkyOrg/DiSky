package info.itsthesky.disky.api;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.registrations.Classes;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

public abstract class StupidSimplePropertyExpression<F, T> extends SimplePropertyExpression<F, T> {

    @SuppressWarnings({"unchecked" })
    public StupidSimplePropertyExpression(String property) {
        this(property, Classes.getExactClassName((Class<T>) new TypeToken<T>(){}.getType()) + "s");
    }

    @SuppressWarnings({ "unchecked"})
    public StupidSimplePropertyExpression(String property, String fromType) {
        register((Class<? extends Expression<T>>)getClass(), (Class<T>) new TypeToken<T>(){}.getType(), property, fromType);
    }

    @SuppressWarnings({"unchecked" })
    @Override
    public @NotNull Class<? extends T> getReturnType() {
        return (Class<? extends T>) new TypeToken<T>(){}.getType();
    }

    @Override
    protected @NotNull String getPropertyName() {
        return getClass().getName();
    }

}
