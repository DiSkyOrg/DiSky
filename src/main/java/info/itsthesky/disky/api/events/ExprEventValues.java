package info.itsthesky.disky.api.events;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author ItsTheSky
 * */
public class ExprEventValues extends SimpleExpression<Object> {

    static {
        Skript.registerExpression(ExprEventValues.class, Object.class, ExpressionType.SIMPLE,
                "(multiple|list|array) event-<.+>");
    }

    private static final HashMap<Class<? extends Event>, List<EventValue<?, ?>>> eventValues = new HashMap<>();
    public static void registerEventValue(Class<? extends Event> event, EventValue<?, ?> value) {
        if (!eventValues.containsKey(event))
            eventValues.put(event, new ArrayList<>());
        eventValues.get(event).add(value);
    }

    private EventValue<?, ?> value;

    @Override
    public boolean init(final Expression<?> @NotNull [] exprs, final int matchedPattern, final @NotNull Kleenean isDelayed, final @NotNull ParseResult parser) {
        final String name = parser.expr.split("event-")[1];
        final List<EventValue<?, ?>> values = eventValues.getOrDefault(ParserInstance.get().getCurrentEvents()[0], new ArrayList<>());
        if (values.isEmpty() || values.stream().noneMatch(v -> v.getName().equalsIgnoreCase(name))) {
            Skript.error("Unknown event value '" + name + "'");
            return false;
        }
        value = values.stream().filter(v -> v.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        if (value == null)
            throw new NullPointerException("Event value '" + name + "' is null");
        return true;
    }

    @Override
    protected Object @NotNull [] get(final @NotNull Event e) {
        return value.getObject(e);
    }

    @Override
    public @NotNull Class<?> getReturnType() {
        return value.getC();
    }

    @Override
    public @NotNull String toString(final @Nullable Event e, final boolean debug) {
        return "event-" + value.getName();
    }

    @Override
    public boolean isSingle() {
        return false;
    }

}