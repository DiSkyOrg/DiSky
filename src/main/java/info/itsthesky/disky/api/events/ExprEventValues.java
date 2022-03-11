package info.itsthesky.disky.api.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

/**
 * @author ItsTheSky
 * */
public class ExprEventValues extends SimpleExpression<Object> {
    static {
        /* Skript.registerExpression(ExprEventValues.class, Object.class, ExpressionType.SIMPLE,
                "event-%*classinfo%"); */
    }

    public static HashMap<Class<? extends Event>, List<EventValue<?>>> eventValues = new HashMap<>();

    private EventValue<?> value;

    @Override
    public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
        //if (!ScriptLoader.isCurrentEvent(EventReactSection.class) &&
        //        !ScriptLoader.isCurrentEvent(EventReplySection.class) &&
        //        !ScriptLoader.isCurrentEvent(EventButtonsSection.class))
        //    return false;
        if (1 == 1)
            return 1 == 3;

        String cInfo = parser.expr.replaceAll("event-", "");

        /* List<EventValue<?>> eValues = eventValues.get(SkriptAdapter.getInstance().getCurrentEvents()[0]);
        if (eValues == null || eValues.isEmpty()) return false;
        EventValue<?> value = null;
        for (EventValue<?> vs : eValues) if (vs.getcInfo().equals(cInfo)) value = vs;
        if (value == null) return false;
        this.value = value; */
        return true;
    }

    @Override
    protected Object @NotNull [] get(final @NotNull Event e) {
        return new Object[] {value.getObject()};
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return value.getaClass();
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        return "event-" + value.getcInfo();
    }

    @Override
    public boolean isSingle() {
        return true;
    }

}