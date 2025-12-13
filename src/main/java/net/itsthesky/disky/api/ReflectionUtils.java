package net.itsthesky.disky.api;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.ExpressionInfo;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SyntaxElementInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Just a simple reflection class, just to not depend on Skript 2.2+ (I think it is the only thing I use from it)
 *
 * @author Sky
 */
public class ReflectionUtils {

    public static Method getMethod(Class<?> clz, String method, Class<?>... parameters) {
        try {
            return clz.getDeclaredMethod(method, parameters);
        } catch (Exception ignored) { }
        return null;
    }

    public static Object cast(Object instance, Class<?> classToCastIn) {
        return classToCastIn.cast(instance);
    }

    @SuppressWarnings("unchecked")
    public static Object invokeEnum(Class<?> enumClass, String enumValue) {
        return Enum.valueOf((Class<Enum>) enumClass, enumValue);
    }

    public static <T> Constructor<T> getConstructor(Class<T> clz, Class<?>...parameters){
        try {
            return clz.getConstructor(parameters);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getClass(String name) {
        try {
            return Class.forName(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param clazz The class of the method
     * @param method The method to invoke
     * @param instance The instance for the method to be invoked from
     * @param parameters The parameters of the method
     * @return The result of the method, or null if the method was null or the invocation failed
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Class<?> clazz, String method, Object instance, Object... parameters) {
        try {
            Class<?>[] parameterTypes = new Class<?>[parameters.length];
            int x = 0;

            for (Object obj : parameters)
                parameterTypes[x++] = obj.getClass();

            Method m = clazz.getDeclaredMethod(method, parameterTypes);
            m.setAccessible(true);

            return (T) m.invoke(instance, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodEx(Class<?> clazz, String method, Object instance, Object... parameters) throws Exception {
        Class<?>[] parameterTypes = new Class<?>[parameters.length];
        int x = 0;
        for (Object obj : parameters)
            parameterTypes[x++] = obj.getClass();
        Method m = clazz.getDeclaredMethod(method, parameterTypes);
        m.setAccessible(true);
        return (T) m.invoke(instance, parameters);
    }

    public static boolean classExist(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * @param clz The class to create the instance of.
     * @return A instance object of the given class.
     */
    public static <T> T newInstance(Class<T> clz) {
        try {
            Constructor<T> c = clz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T newInstance(Class<T> clz, Object... args) {
        List<Class<?>> classes = new ArrayList<>();
        for (Object o : args)
            classes.add(o.getClass());
        Class<?>[] argClasses = classes.toArray(new Class<?>[0]);

        try {
            Constructor<T> c = clz.getDeclaredConstructor(argClasses);
            c.setAccessible(true);
            return c.newInstance(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param from The class of the field
     * @param obj The instance of the class - you can use null if the field is static
     * @param field The field name
     */
    public static <T> void setField(Class<T> from, Object obj, String field, Object newValue) {
        try {
            Field f = from.getDeclaredField(field);
            f.setAccessible(true);
            f.set(obj, newValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object arg) {
        try {
            return (T) field.get(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getFieldValue(Field field) {
        return getFieldValue(field, null);
    }

    public static <T> T getFieldValue(Class<?> target, String field) {
        return getFieldValue(getField(target, field));
    }

    public static <T> T getFieldValue(Class<?> target, String field, Object instance) {
        return getFieldValue(getField(target, field), instance);
    }

    public static <T> T getFieldValueViaInstance(Object instance, String field) {
        return getFieldValue(instance.getClass(), field, instance);
    }

    public static void setFieldValue(Field field, Object target, Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setFieldValue(Field field, Object value) {
        setFieldValue(field, null, value);
    }

    /**
     * @param from The class of the field
     * @param obj The instance of the class - you can use null if the field is static
     * @param field The field name
     * @return The field or null if it couldn't be gotten
     */
    @SuppressWarnings("unchecked")
    public static <T> T getField(Class<?> from, Object obj, String field) {
        try {
            Field f = from.getDeclaredField(field);
            f.setAccessible(true);
            return (T) f.get(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> from, String field) {
        try {
            Field f = from.getDeclaredField(field);
            f.setAccessible(true);
            return f;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void setFinalCollection(Class<?> clazz, String fieldName, Collection collection) {
        try {
            final Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (!(field.get(null) instanceof Collection))
                return;
            ((Collection) field.get(null)).clear();
            ((Collection) field.get(null)).addAll(collection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getCurrentClass() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ReflectionUtils.class.getName()) && i > 2 && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public static void removeElement(String clazz, String... fields) throws Exception {
        if (!classExist(clazz))
            return;
        final Class<?> clz = Class.forName(clazz);

        for (String f : fields) {
            final Field field = Skript.class.getDeclaredField(f);
            field.setAccessible(true);

            if (f.equalsIgnoreCase("expressions")) {
                ((Collection<ExpressionInfo<?, ?>>) field.get(null))
                        .removeIf(info -> info.getElementClass().equals(clz));
            } else {
                ((Collection<SyntaxElementInfo<? extends Effect>>) field.get(null))
                        .removeIf(info -> info.getElementClass().equals(clz));
            }

            field.setAccessible(false);
        }

        if (Arrays.asList(fields).contains("expressions")) {
            final Field exprsIndexes = Skript.class.getDeclaredField("expressionTypesStartIndices");
            exprsIndexes.setAccessible(true);

            for (ExpressionType type : ExpressionType.values()) {
                final int[] ints = (int[]) exprsIndexes.get(null);
                ints[type.ordinal()] = ints[type.ordinal()] - 1;
            }

            exprsIndexes.setAccessible(false);
        }
    }

    public static <T extends Enum<T>> @Nullable T parseEnum(Class<T> clazz, String input) {
        try {
            return Enum.valueOf(clazz, input.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Class<?> getGenericType(Field field) {
        return (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    public static Class<?> getCallerClass(Predicate<String> filter) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stack.length; i++) {
            try {
                String className = stack[i].getClassName();
                if (filter.test(className)) {
                    return Class.forName(className);
                }
            } catch (ClassNotFoundException ignored) {}
        }
        return null;
    }
}