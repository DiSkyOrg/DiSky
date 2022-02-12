package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class MultipleChangeableSimplePropertyExpression<F, T> extends MultiplyPropertyExpression<F, T> implements DiSkyChangerElement {

    @Override
    public final void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        change(e, delta, findAny(), mode);
    }

}