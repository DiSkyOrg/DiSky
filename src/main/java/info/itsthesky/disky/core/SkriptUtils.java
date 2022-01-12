package info.itsthesky.disky.core;

import ch.njol.skript.lang.Expression;
import org.bukkit.event.Event;

public final class SkriptUtils {

    @Deprecated
    public static <T> T verifyVar(Expression<T> expression, Event e, T defaultValue) {
        return expression == null ? defaultValue :
                (expression.getSingle(e) == null ? defaultValue : expression.getSingle(e));
    }

}
