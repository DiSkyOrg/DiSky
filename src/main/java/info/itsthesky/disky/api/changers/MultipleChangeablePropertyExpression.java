package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import info.itsthesky.disky.api.skript.MultiplyPropertyExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class MultipleChangeablePropertyExpression<F, T> extends MultiplyPropertyExpression<F, T> implements DiSkyChangerElement {

    @Override
    public final void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        change(e, delta, findAny(), mode);
    }

}
