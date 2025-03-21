package net.itsthesky.disky.api.skript.reflects;

import net.itsthesky.disky.api.skript.MultipleGetterExpression;
import net.itsthesky.disky.api.skript.SimpleGetterExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class MultipleReflectGetterExpression extends MultipleGetterExpression<Object, Event> {

    @Override
    protected String getValue() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    protected Class<Event> getEvent() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    protected Object[] gets(Event event) {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
    }
}
