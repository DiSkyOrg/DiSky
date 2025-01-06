package info.itsthesky.disky.api.datastruct;

import ch.njol.skript.lang.ExpressionType;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.DiSkyRegistry;
import info.itsthesky.disky.api.datastruct.base.DataStruct;
import info.itsthesky.disky.elements.datastructs.structures.EmbedStructure;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.util.concurrent.atomic.AtomicInteger;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Registry for easy-to-register data structure elements, that wouldn't
 * require complex handling.
 * @see ReflectBasicDataStructure
 * @see EmbedStructure
 */
public class EasyDSRegistry {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public static <T, D extends DataStruct<T>> void registerBasicDataStructure(Class<D> clazz,
                                                                               Class<T> returnType,
                                                                               String pattern,
                                                                               String toString) {
        try {

            final Class<?> elementClass = new ByteBuddy()
                    .redefine(ReflectBasicDataStructure.class)
                    .name("info.itsthesky.disky.elements.reflects.ReflectDataStructureElement_" + COUNT.incrementAndGet())

                    .method(named("getReturnType")).intercept(MethodDelegation.to(new ReturnClassMethodInterceptor(returnType)))
                    .method(named("getDataStructClass")).intercept(MethodDelegation.to(new ReturnClassMethodInterceptor(clazz)))
                    .method(named("toString")).intercept(MethodDelegation.to(new ReturnStringMethodInterceptor(toString)))

                    .make()
                    .load(clazz.getClassLoader())
                    .getLoaded();

            DiSkyRegistry.registerExpression(
                    (Class) elementClass,
                    returnType,
                    ExpressionType.SIMPLE,
                    pattern
            );
            DiSky.debug("Registered new data structure element: " + elementClass.getSimpleName() + " with pattern: " + pattern);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    //region [ Interceptors ]

    public static class ReturnClassMethodInterceptor {
        private final Class<?> returnType;
        public ReturnClassMethodInterceptor(Class<?> returnType) {
            this.returnType = returnType;
        }
        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments) {
            return returnType;
        }
    }

    public static class ReturnStringMethodInterceptor {
        private final String returnString;
        public ReturnStringMethodInterceptor(String returnString) {
            this.returnString = returnString;
        }
        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments) {
            return returnString;
        }
    }

    //endregion

}
