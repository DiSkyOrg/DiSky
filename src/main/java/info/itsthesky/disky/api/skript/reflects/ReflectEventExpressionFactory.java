package info.itsthesky.disky.api.skript.reflects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.ExpressionType;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyRegistry;
import info.itsthesky.disky.api.skript.SimpleGetterExpression;
import info.itsthesky.disky.elements.events.interactions.MessageCommandEvent;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.dv8tion.jda.api.entities.Message;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxOrigin;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @author ItsTheSky
 */
public class ReflectEventExpressionFactory {

    private static final AtomicInteger COUNT = new AtomicInteger();

    public static <T, E extends Event> void registerEventExpression(
            String pattern,
            Class<E> eventClass,
            Class<T> expressionClass,
            Function<E, T> converter
    ) {
        if (true)
            return;

        COUNT.incrementAndGet();
        try {

            final Class<?> elementClass = new ByteBuddy()
                    .redefine(ReflectGetterExpression.class)
                    .name("info.itsthesky.disky.elements.reflects.ReflectGetterExpression_" + COUNT.incrementAndGet())

                    .method(named("convert")).intercept(MethodDelegation.to(new ReflectClassFactory.ConvertMethodInterceptor<>(converter)))
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

    private static String stripPattern(String pattern) {
        return pattern
                .replace("(", "")
                .replace(")", "")
                .replace("[", "")
                .replace("]", "")
                .replace("|", "");
    }
}
