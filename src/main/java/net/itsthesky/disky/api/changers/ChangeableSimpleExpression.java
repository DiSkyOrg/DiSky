package net.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.util.SimpleExpression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class ChangeableSimpleExpression<T> extends SimpleExpression<T> implements DiSkyChangerElement {

    @Override
    public final void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        change(e, delta, findAny(), mode);
    }

}
