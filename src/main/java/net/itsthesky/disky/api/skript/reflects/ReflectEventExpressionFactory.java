package net.itsthesky.disky.api.skript.reflects;

import ch.njol.skript.lang.ExpressionType;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import org.bukkit.event.Event;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author ItsTheSky
 */
public class ReflectEventExpressionFactory {

    private static final AtomicInteger COUNT = new AtomicInteger(1);

    public static <T, E extends Event> void registerEventExpression(
            String pattern,
            Class<E> eventClass,
            Class<T> expressionClass,
            Function<E, T> converter
    ) {
        registerSingleEventExpression(pattern, eventClass, expressionClass, converter);
    }

    public static <T, E extends Event> void registerSingleEventExpression(
            String pattern,
            Class<E> eventClass,
            Class<T> expressionClass,
            Function<E, T> converter
    ) {
        try {

            final Class<?> elementClass = new ByteBuddy()
                    .redefine(ReflectGetterExpression.class)
                    .name("net.itsthesky.disky.elements.reflects.ReflectGetterExpression_" + COUNT.incrementAndGet())

                    .method(named("convert")).intercept(MethodDelegation.to(new ReflectClassFactory.SingleConvertMethodInterceptor<>(converter)))
                    .method(named("getPropertyName")).intercept(MethodDelegation.to(new ReflectClassFactory.PropertyNameMethodInterceptor(stripPattern(pattern))))

                    .method(named("getEvent")).intercept(MethodDelegation.to(new ReflectClassFactory.ClassResultMethodInterceptor(eventClass)))
                    .method(named("getReturnType")).intercept(MethodDelegation.to(new ReflectClassFactory.ClassResultMethodInterceptor(expressionClass)))

                    .make()
                    .load(ReflectGetterExpression.class.getClassLoader())
                    .getLoaded();

            DiSkyRegistry.registerExpression(
                    (Class) elementClass,
                    expressionClass,
                    ExpressionType.SIMPLE,
                    pattern);

            DiSky.debug("Registered the event expression " + pattern + " with the class " + elementClass.getName());

        } catch (Exception ex) {
            throw new RuntimeException("Cannot register the event expression " + pattern, ex);
        }
    }

    public static <T, E extends Event> void registerListEventExpression(
            String pattern,
            Class<E> eventClass,
            Class<T> expressionClass,
            Function<E, T[]> converter
    ) {
        try {

            final Class<?> elementClass = new ByteBuddy()
                    .redefine(MultipleReflectGetterExpression.class)
                    .name("net.itsthesky.disky.elements.reflects.MultipleReflectGetterExpression_" + COUNT.incrementAndGet())

                    .method(named("gets")).intercept(MethodDelegation.to(new ReflectClassFactory.MultipleConvertMethodInterceptor<>(converter)))
                    .method(named("getPropertyName")).intercept(MethodDelegation.to(new ReflectClassFactory.PropertyNameMethodInterceptor(stripPattern(pattern))))

                    .method(named("getEvent")).intercept(MethodDelegation.to(new ReflectClassFactory.ClassResultMethodInterceptor(eventClass)))
                    .method(named("getReturnType")).intercept(MethodDelegation.to(new ReflectClassFactory.ClassResultMethodInterceptor(expressionClass)))

                    .make()
                    .load(MultipleReflectGetterExpression.class.getClassLoader())
                    .getLoaded();

            DiSkyRegistry.registerExpression(
                    (Class) elementClass,
                    expressionClass,
                    ExpressionType.SIMPLE,
                    pattern);

            DiSky.debug("Registered the event expression " + pattern + " with the class " + elementClass.getName());

        } catch (Exception ex) {
            throw new RuntimeException("Cannot register the event expression " + pattern, ex);
        }
    }

    private static String stripPattern(String pattern) {
        return pattern
                .replace("(", "")
                .replace(")", "")
                .replace("[", "")
                .replace("]", "")
                .replace("|", "");
    }
}
