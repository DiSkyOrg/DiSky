package net.itsthesky.diskytest.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import net.itsthesky.diskytest.fake.FakeEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * <p>Reflective fake-entity factory exposed to Skript.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 *   set {_role}  to new fake "Role" with [{_guild}, "admin"]
 *   set {_emoji} to new fake "RichCustomEmoji" with [{_guild}, {_self}, "confetti", false]
 *   set {_user}  to new fake "User" with [{_fakeJDA}, "Alice", false]
 * }</pre>
 *
 * <p>The first string is the <b>JDA interface simple name</b> (e.g. {@code Role},
 * {@code RichCustomEmoji}, {@code Member}, {@code User}, {@code TextChannel}).
 * The factory resolves it to the matching {@code FakeXxx} class by scanning
 * {@link #CANDIDATE_PACKAGES}, then picks its <b>first declared public constructor</b>
 * and tries to coerce each Skript argument into the expected parameter type.</p>
 *
 * <h3>Argument coercion rules</h3>
 * <ul>
 *   <li>If the parameter type extends {@link FakeEntity} and the argument is a
 *       JDA proxy or a {@code FakeEntity}, it is unwrapped via
 *       {@link FakeEntity#unwrap(Object)}.</li>
 *   <li>If the target is {@code FakeUser} but the caller passes a {@code FakeMember}
 *       (common when using {@code {_self}} or {@code {_member}}), we auto-adapt by
 *       calling {@code getFakeUser()} on the member — saves boilerplate in tests.</li>
 *   <li>Primitive boxing/unboxing and {@code Number} → {@code int/long/short/byte/float/double}
 *       widening are handled.</li>
 *   <li>Everything else is passed as-is; a {@link ClassCastException} here surfaces
 *       as a Skript runtime warning and {@code null} is returned.</li>
 * </ul>
 *
 * <p>The returned value is the fake's JDA-interface proxy (via {@link FakeEntity#asProxy()}),
 * so it's directly usable in any Skript DiSky syntax.</p>
 */
public class ExprNewFake extends SimpleExpression<Object> {

    /**
     * Packages scanned (in order) to resolve a Fake simple name.
     * Add more here if you split the fake package later.
     */
    private static final String[] CANDIDATE_PACKAGES = {
            "net.itsthesky.diskytest.fake",
            "net.itsthesky.diskytest.fake.action",
    };

    /** Cache: interface simple name → resolved FakeXxx class (or a sentinel for misses). */
    private static final Map<String, Class<?>> RESOLVE_CACHE = new HashMap<>();
    private static final Class<?> NOT_FOUND_SENTINEL = Void.class;

    static {
        Skript.registerExpression(
                ExprNewFake.class,
                Object.class,
                ExpressionType.COMBINED,
                "[a] new fake %string% with [arg[ument]s] %objects%",
                "[a] new fake %string%"   // zero-argument constructor form
        );
    }

    private Expression<String> exprName;
    private @Nullable Expression<?> exprArgs;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int matchedPattern, @NotNull Kleenean isDelayed, @NotNull ParseResult result) {
        exprName = (Expression<String>) exprs[0];
        exprArgs = matchedPattern == 0 ? LiteralUtils.defendExpression(exprs[1]) : null;
        return true;
    }

    @Override
    protected Object @Nullable [] get(@NotNull Event event) {
        String simpleName = exprName.getSingle(event);
        if (simpleName == null || simpleName.isBlank()) {
            Skript.warning("'new fake' called with a null/empty interface name");
            return new Object[0];
        }

        Class<?> fakeClass = resolveFakeClass(simpleName.trim());
        if (fakeClass == null) {
            Skript.warning("'new fake \"" + simpleName + "\"': no FakeEntity class found. "
                    + "Expected 'Fake" + simpleName + "' in one of " + Arrays.toString(CANDIDATE_PACKAGES));
            return new Object[0];
        }

        Object[] rawArgs = exprArgs == null ? new Object[0] : exprArgs.getAll(event);
        if (rawArgs == null) rawArgs = new Object[0];

        // Pick the first public constructor whose arity matches. If several match,
        // pick the one with the most specific parameter types (simple heuristic:
        // first declared).
        Constructor<?> chosen = null;
        for (Constructor<?> ctor : fakeClass.getDeclaredConstructors()) {
            if (!Modifier.isPublic(ctor.getModifiers())) continue;
            if (ctor.getParameterCount() == rawArgs.length) {
                chosen = ctor;
                break;
            }
        }

        if (chosen == null) {
            String expected = Arrays.stream(fakeClass.getDeclaredConstructors())
                    .filter(c -> Modifier.isPublic(c.getModifiers()))
                    .map(c -> "(" + c.getParameterCount() + " args: "
                            + describe(c.getParameterTypes()) + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("<no public constructor>");
            Skript.warning("'new fake \"" + simpleName + "\"': no constructor with "
                    + rawArgs.length + " argument(s). Expected: " + expected);
            return new Object[0];
        }

        Object[] coerced;
        try {
            coerced = coerceArgs(chosen.getParameterTypes(), rawArgs);
        } catch (RuntimeException ex) {
            Skript.warning("'new fake \"" + simpleName + "\"': argument coercion failed: "
                    + ex.getMessage());
            return new Object[0];
        }

        Object fakeInstance;
        try {
            fakeInstance = chosen.newInstance(coerced);
        } catch (ReflectiveOperationException ex) {
            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
            Skript.warning("'new fake \"" + simpleName + "\"': constructor threw "
                    + cause.getClass().getSimpleName() + ": " + cause.getMessage());
            return new Object[0];
        }

        if (!(fakeInstance instanceof FakeEntity<?> fake)) {
            Skript.warning("'new fake \"" + simpleName + "\"': class " + fakeClass.getName()
                    + " does not extend FakeEntity");
            return new Object[0];
        }

        return new Object[]{ fake.asProxy() };
    }

    // ------ Resolution & coercion helpers ------

    private static @Nullable Class<?> resolveFakeClass(String interfaceSimpleName) {
        Class<?> cached = RESOLVE_CACHE.get(interfaceSimpleName);
        if (cached == NOT_FOUND_SENTINEL) return null;
        if (cached != null) return cached;

        String target = "Fake" + interfaceSimpleName;
        for (String pkg : CANDIDATE_PACKAGES) {
            try {
                Class<?> candidate = Class.forName(pkg + "." + target);
                if (FakeEntity.class.isAssignableFrom(candidate)) {
                    RESOLVE_CACHE.put(interfaceSimpleName, candidate);
                    return candidate;
                }
            } catch (ClassNotFoundException ignored) {
                // keep searching
            }
        }

        RESOLVE_CACHE.put(interfaceSimpleName, NOT_FOUND_SENTINEL);
        return null;
    }

    /**
     * Coerces each raw Skript argument into the constructor's expected parameter type.
     * Throws {@link IllegalArgumentException} on incompatibility (includes the index
     * and types in the message for debuggability).
     */
    private static Object[] coerceArgs(Class<?>[] paramTypes, Object[] rawArgs) {
        Object[] out = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            out[i] = coerceArg(paramTypes[i], rawArgs[i], i);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private static @Nullable Object coerceArg(Class<?> paramType, Object raw, int index) {
        if (raw == null) {
            if (paramType.isPrimitive()) {
                throw new IllegalArgumentException("arg[" + index + "]: cannot pass null to primitive "
                        + paramType.getSimpleName());
            }
            return null;
        }

        // 1. Already assignable (String → String, FakeGuild → FakeGuild, etc.)
        if (paramType.isInstance(raw)) return raw;

        // 2. Unwrap JDA proxies into FakeEntity subclasses where needed.
        if (FakeEntity.class.isAssignableFrom(paramType)) {
            FakeEntity<?> unwrapped = FakeEntity.unwrap(raw);
            if (unwrapped == null) {
                throw new IllegalArgumentException("arg[" + index + "]: expected "
                        + paramType.getSimpleName() + ", got " + describe(raw));
            }
            if (paramType.isInstance(unwrapped)) return unwrapped;

            // 2b. Special adaptation: FakeMember → FakeUser (extract the underlying user).
            // Written reflectively so we don't hardcode a dependency here.
            try {
                Object adapted = tryAdapt(unwrapped, paramType);
                if (adapted != null) return adapted;
            } catch (ReflectiveOperationException ignored) { /* fall through */ }

            throw new IllegalArgumentException("arg[" + index + "]: expected "
                    + paramType.getSimpleName() + ", got " + unwrapped.getClass().getSimpleName()
                    + " (no adapter available)");
        }

        // 3. Primitive / boxed numeric coercion.
        if (paramType.isPrimitive() || Number.class.isAssignableFrom(paramType)
                || paramType == Boolean.class || paramType == Character.class) {
            Object coerced = coercePrimitive(paramType, raw);
            if (coerced != null) return coerced;
        }

        throw new IllegalArgumentException("arg[" + index + "]: expected "
                + paramType.getSimpleName() + ", got " + describe(raw));
    }

    /**
     * Attempts {@code source.getFakeXxx()} for common conventional adapters
     * (e.g. FakeMember.getFakeUser()). Silent on missing methods.
     */
    private static @Nullable Object tryAdapt(FakeEntity<?> source, Class<?> targetType)
            throws ReflectiveOperationException {
        String adapterName = "get" + targetType.getSimpleName();
        try {
            var m = source.getClass().getMethod(adapterName);
            Object result = m.invoke(source);
            return targetType.isInstance(result) ? result : null;
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private static @Nullable Object coercePrimitive(Class<?> target, Object raw) {
        // Booleans
        if ((target == boolean.class || target == Boolean.class) && raw instanceof Boolean b) return b;

        // Characters
        if ((target == char.class || target == Character.class)) {
            if (raw instanceof Character c) return c;
            if (raw instanceof String s && s.length() == 1) return s.charAt(0);
        }

        // Numerics
        if (raw instanceof Number n) {
            if (target == int.class || target == Integer.class) return n.intValue();
            if (target == long.class || target == Long.class) return n.longValue();
            if (target == double.class || target == Double.class) return n.doubleValue();
            if (target == float.class || target == Float.class) return n.floatValue();
            if (target == short.class || target == Short.class) return n.shortValue();
            if (target == byte.class || target == Byte.class) return n.byteValue();
        }
        return null;
    }

    private static String describe(Object o) {
        if (o == null) return "null";
        return o.getClass().getSimpleName();
    }

    private static String describe(Class<?>[] types) {
        if (types.length == 0) return "<none>";
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < types.length; i++) {
            if (i > 0) b.append(", ");
            b.append(types[i].getSimpleName());
        }
        return b.toString();
    }

    // ------ SimpleExpression boilerplate ------

    @Override
    public boolean isSingle() { return true; }

    @Override
    public @NotNull Class<?> getReturnType() { return Object.class; }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        String argsStr = exprArgs == null ? "" : " with " + exprArgs.toString(event, debug);
        return "new fake " + exprName.toString(event, debug) + argsStr;
    }
}