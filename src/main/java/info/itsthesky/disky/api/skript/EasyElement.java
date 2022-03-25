package info.itsthesky.disky.api.skript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.parser.ParserInstance;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Simple class that contains some useful methods for variable, expressions and more.
 * @author ItsTheSky
 */
public abstract class EasyElement extends Effect {

    /**
     * Validate a specific expression, to see if it's a variable or not.
     * @param expression The expression to validate
     * @return True if the expression is a variable, else false
     */
    public static boolean validate(Expression<?> expression) {
        return expression instanceof Variable<?>;
    }

    /**
     * Parse a single {@link Expression}, if the expression itself or its content is null, the defaultValue will be returned.
     * @param expression   The expression to parse
     * @param e            The {@link Event} to use on parsing
     * @param defaultValue The default value
     * @return The parsed value
     */
    public static <T> T parseSingle(Expression<T> expression, Event e, T defaultValue) {
        if (expression == null)
            return defaultValue;
        final @Nullable T value = expression.getSingle(e);
        return value == null ? defaultValue : value;
    }

    /**
     * Parse a list {@link Expression}, if the expression itself or its content is null, defaultValues will be returned.
     * @param expression   The expression to parse
     * @param e            The {@link Event} to use on parsing
     * @param defaultValues The default values
     * @return The parsed values
     */
    public static <T> T[] parseList(Expression<T> expression, Event e, T[] defaultValues) {
        if (expression == null)
            return defaultValues;
        final @Nullable T[] values = expression.getArray(e);
        return values == null || values.length == 0 ? defaultValues : values;
    }

    /**
     * Check wether any of the argument array is null.
     * @param objects The objects to test
     * @return True if objects contains a null object, else false
     */
    public static boolean anyNull(Object... objects) {
        return Arrays.stream(objects).anyMatch(Objects::isNull);
    }

    /**
     * Advanced method, checking if it's a variable and if the type (single or list) is also validate.
     * @param expression   The expression to validate
     * @param shouldBeList Either the input expression should be a single or list variable
     * @return True if {@link EasyElement#validate(Expression)} and the expression is a list or not according to the argument, else false.
     */
    public static boolean validate(Expression<?> expression, boolean shouldBeList) {
        if (!validate(expression)) {
            Skript.error("Invalid expression, require a variable: " + expression.toString(null, false));
            return false;
        }
        return shouldBeList ? ((Variable<?>) expression).isList()
                : expression.isSingle();
    }

    public static boolean containsInterfaces(@NotNull Class<?> clazz) {
        return Stream
                .of(getCurrentEvents())
                .filter(Objects::nonNull)
                .anyMatch(c -> Arrays.asList(c.getInterfaces()).contains(clazz));
    }

    public static boolean eventsMatch(@NotNull Predicate<Class<? extends Event>> predicate) {
        return Stream.of(getCurrentEvents()).anyMatch(predicate);
    }

    public static Class<? extends Event>[] getCurrentEvents() {
        return ParserInstance.get().getCurrentEvents();
    }

    public static boolean containsEvent(@NotNull Class<? extends Event> clazz) {
        return Arrays.asList(getCurrentEvents()).contains(clazz);
    }

    @SafeVarargs
    public static <T> boolean equalAny(T base, T... entities) {
        return Arrays
                .stream(entities)
                .anyMatch(o -> o == base);
    }

	public static boolean isChangerMode(Changer.ChangeMode mode) {
        return equalAny(mode, Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.RESET, Changer.ChangeMode.REMOVE_ALL);
	}
}
