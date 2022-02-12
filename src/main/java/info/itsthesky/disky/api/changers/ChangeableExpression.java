package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import info.itsthesky.disky.elements.changers.EffChange;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class ChangeableExpression<T> implements Expression<T>, DiSkyChangerElement {

    @Override
    public final void change(@NotNull Event e, Object @NotNull [] delta, Changer.@NotNull ChangeMode mode) {
        change(e, delta, findAny(), mode);
    }

}