package info.itsthesky.disky.api.changers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class ChangeableExpression implements Expression, DiSkyChangerElement {

    @Override
    public final void change(@NotNull Event e, @NotNull Object[] delta, @NotNull Changer.ChangeMode mode) {
        change(e, delta, findAny(), mode);
    }

}
