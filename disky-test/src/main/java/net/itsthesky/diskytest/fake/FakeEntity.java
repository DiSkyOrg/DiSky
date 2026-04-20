package net.itsthesky.diskytest.fake;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Base class for all in-memory JDA fakes.
 *
 * <p>Each fake implements a JDA interface (e.g. {@link net.dv8tion.jda.api.JDA},
 * {@link net.dv8tion.jda.api.entities.Guild}). Because those interfaces have hundreds
 * of methods we don't want to enumerate by hand, we use a dynamic {@link Proxy}:
 * each {@code FakeXxx} subclass simply declares the methods it actually implements
 * (with the same signature as the JDA interface), and the proxy routes calls to
 * matching subclass methods or throws {@link UnsupportedOperationException} for
 * methods that haven't been implemented yet.
 *
 * <p>This follows the MockBukkit philosophy: implement on demand, stub the rest
 * with a clear "not implemented yet" error pointing at the missing piece.
 *
 * @param <I> the JDA interface this fake implements
 */
public abstract class FakeEntity<I> implements InvocationHandler {

    /** Snowflake-style ID generator. Every fake entity gets a unique 17-19 digit ID. */
    private static final AtomicLong SNOWFLAKE_COUNTER = new AtomicLong(100_000_000_000_000_000L);

    public static long nextSnowflake() {
        return SNOWFLAKE_COUNTER.incrementAndGet();
    }

    private final Class<I> interfaceClass;
    /** All interfaces the proxy will declare — the primary + any extras. */
    private final Class<?>[] proxyInterfaces;
    private I proxy;

    protected FakeEntity(Class<I> interfaceClass, Class<?>... additionalInterfaces) {
        this.interfaceClass = interfaceClass;
        // Deduplicate: primary interface first, then extras (e.g. from allInterfacesOf).
        Set<Class<?>> all = new LinkedHashSet<>();
        all.add(interfaceClass);
        for (Class<?> c : additionalInterfaces) all.add(c);
        this.proxyInterfaces = all.toArray(new Class[0]);
    }

    /**
     * Collects every interface (and their super-interfaces) that {@code implClassName}
     * transitively implements by walking its whole class hierarchy.
     *
     * <p>Pass the result as {@code additionalInterfaces} so the dynamic proxy
     * declares the same interface set as the real JDA implementation — this prevents
     * ClassCastExceptions when DiSky or JDA internally casts to union interfaces like
     * {@link net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion}.
     */
    protected static Class<?>[] allInterfacesOf(Class<?> implClass) {
        Set<Class<?>> seen = new LinkedHashSet<>();
        collectInterfaces(implClass, seen);
        return seen.toArray(new Class[0]);
    }

    private static void collectInterfaces(Class<?> clazz, Set<Class<?>> acc) {
        if (clazz == null || clazz == Object.class) return;
        for (Class<?> iface : clazz.getInterfaces()) {
            if (acc.add(iface)) {
                collectInterfaces(iface, acc); // collect parent interfaces too
            }
        }
        collectInterfaces(clazz.getSuperclass(), acc);
    }

    /**
     * If {@code value} is a JDA proxy whose invocation handler is a {@link FakeEntity},
     * returns that handler. Otherwise returns {@code null}.
     *
     * <p>Used by the reflective factory to unwrap Skript variables (which hold
     * JDA-interface proxies) back into their concrete fake implementations, so
     * constructors that expect {@code FakeGuild}, {@code FakeUser}, etc. can be
     * invoked with arguments coming from Skript.
     */
    public static @Nullable FakeEntity<?> unwrap(Object value) {
        if (value == null) return null;
        if (value instanceof FakeEntity<?> fe) return fe;
        if (!Proxy.isProxyClass(value.getClass())) return null;
        InvocationHandler handler = Proxy.getInvocationHandler(value);
        return handler instanceof FakeEntity<?> fe ? fe : null;
    }

    /**
     * Returns the dynamic proxy implementing the JDA interface(s).
     * The proxy is created lazily and cached.
     */
    @SuppressWarnings("unchecked")
    public final I asProxy() {
        if (proxy == null) {
            // Use the primary interface's classloader — all JDA interfaces are in the same jar.
            ClassLoader loader = interfaceClass.getClassLoader();
            proxy = (I) Proxy.newProxyInstance(loader, proxyInterfaces, this);
        }
        return proxy;
    }

    @Override
    public final Object invoke(Object proxyInstance, Method method, Object[] args) throws Throwable {
        // Object methods (equals/hashCode/toString) are always handled by `this`.
        if (method.getDeclaringClass() == Object.class) {
            try {
                return method.invoke(this, args);
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }

        // Look for a method on this fake with the exact same signature.
        try {
            Method local = this.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (local.getDeclaringClass() != FakeEntity.class
                    && local.getDeclaringClass() != InvocationHandler.class
                    && local.getDeclaringClass() != Object.class) {
                try {
                    return local.invoke(this, args);
                } catch (InvocationTargetException ex) {
                    throw ex.getCause();
                }
            }
        } catch (NoSuchMethodException ignored) {
            // fall through to "not implemented"
        }

        throw notImplemented(method);
    }

    protected UnsupportedOperationException notImplemented(Method method) {
        String params = Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(", "));
        return new UnsupportedOperationException(
                "FakeJDA: " + this.getClass().getSimpleName() + "."
                        + method.getName() + "(" + params + ") is not yet implemented. "
                        + "Add it to " + this.getClass().getName() + " on demand.");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[proxy=" + interfaceClass.getSimpleName() + "]";
    }
}
