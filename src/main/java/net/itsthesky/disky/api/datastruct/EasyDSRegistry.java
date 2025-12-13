package net.itsthesky.disky.api.datastruct;

import ch.njol.skript.lang.ExpressionType;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.itsthesky.disky.api.datastruct.base.DataStruct;
import net.itsthesky.disky.elements.datastructs.structures.EmbedStructure;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Registry for easy-to-register data structure elements, that wouldn't
 * require complex handling.
 * @see ReflectBasicDataStructure
 * @see EmbedStructure
 */
public class EasyDSRegistry {

    public record DataStructureEntry(Class<? extends DataStruct<?>> clazz, String pattern) {}

    private static final AtomicInteger COUNT = new AtomicInteger(0);
    public static final List<DataStructureEntry> REGISTERED_STRUCTS = new ArrayList<>();

    public static <T, D extends DataStruct<T>> void registerBasicDataStructure(Class<D> clazz,
                                                                               Class<T> returnType,
                                                                               String pattern,
                                                                               String toString) {
        try {

            final Class<?> elementClass = new ByteBuddy()
                    .redefine(ReflectBasicDataStructure.class)
                    .name("net.itsthesky.disky.elements.reflects.ReflectDataStructureElement_" + COUNT.incrementAndGet())

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
            REGISTERED_STRUCTS.add(new DataStructureEntry(clazz, pattern));
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

    public static <T, DS extends DataStruct<T>> DS createDataStructureInstance(Class<DS> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Could not create instance of data structure: " + clazz.getName(), ex);
        }
    }

}
