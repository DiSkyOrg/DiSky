package info.itsthesky.disky.api.skript.reflects.state;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Condition;
import info.itsthesky.disky.api.DiSkyRegistry;
import info.itsthesky.disky.api.skript.INodeHolder;
import info.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import info.itsthesky.disky.managers.ConfigManager;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * Utility to easily and efficiently register (changeable) "state" expressions.
 * <p>
 * For instance, the locked state of a thread channel:
 * <ul>
 *     <li>It'll register a condition, in the form of 'if thread is locked'</li>
 *     <li>It'll register a changeable property, in the form of 'locked state of thread'</li>
 * </ul>
 * </p>
 * @author Sky
 */
public final class SkriptStateRegistry {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public static <T> void register(
            Class<T> typeClass, String typeName,
            String stateName,
            Function<T, Boolean> getter, StateSetter<T> setter
    ) {

        // Condition
        final Class<?> conditionClass = new ByteBuddy()
                .redefine(DiSkyStateCondition.class)
                .name("info.itsthesky.disky.api.skript.reflects.state.CondState_" + COUNT.incrementAndGet())

                .method(named("check")).intercept(MethodDelegation.to(new CheckMethod<>(getter)))
                .method(named("getPropertyName")).intercept(MethodDelegation.to(new PropertyNameMethodInterceptor(stateName + " state")))

                .make()
                .load(typeClass.getClassLoader())
                .getLoaded();
        DiSkyRegistry.registerPropertyCondition((Class<? extends Condition>) conditionClass, stateName, typeName);

        // Property
        final Class<?> propertyClass = new ByteBuddy()
                .redefine(DiSkyStateProperty.class)
                .name("info.itsthesky.disky.api.skript.reflects.state.PropState_" + COUNT.incrementAndGet())

                .method(named("convert")).intercept(MethodDelegation.to(new CheckMethod<>(getter)))
                .method(named("getPropertyName")).intercept(MethodDelegation.to(new PropertyNameMethodInterceptor(stateName + " state")))
                .method(named("change0")).intercept(MethodDelegation.to(new ChangeMethod<>(setter)))

                .make()
                .load(typeClass.getClassLoader())
                .getLoaded();

        DiSkyRegistry.registerProperty(
                (Class<? extends SimplePropertyExpression<Object, Boolean>>) propertyClass,
                Boolean.class,
                stateName + " state",
                typeName
        );
    }

    //region Interceptors

    /**
     * Interceptor for the 'getPropertyName' of the Condition class.
     */
    public static class PropertyNameMethodInterceptor {
        private final String stateName;
        public PropertyNameMethodInterceptor(String stateName) {
            this.stateName = stateName;
        }

        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments) {
            return stateName;
        }
    }

    /**
     * Interceptor for the 'check' method of the Condition class.
     */
    public static class CheckMethod<T> {
        private final Function<T, Boolean> getter;
        public CheckMethod(Function<T, Boolean> getter) {
            this.getter = getter;
        }

        @RuntimeType
        public Object intercept(@This Object element, @AllArguments Object[] allArguments) {
            try {
                return getter.apply((T) allArguments[0]);
            } catch (Exception ex) {
                if (ConfigManager.get("debug", false))
                    ex.printStackTrace();

                DiSkyRuntimeHandler.error(ex, ((INodeHolder) element).getNode());
                return false;
            }
        }
    }

    // ---------------------------------------------------------------------

    /**
     * Interceptor for the 'change0' method of the Property class.
     */
    public static class ChangeMethod<T> {
        private final StateSetter<T> setter;

        public ChangeMethod(StateSetter<T> setter) {
            this.setter = setter;
        }

        @RuntimeType
        public void intercept(@This Object expr, @AllArguments Object[] allArguments) {
            final Event e = (Event) allArguments[0];
            final Object[] delta = (Object[]) allArguments[1];
            final Changer.ChangeMode mode = (Changer.ChangeMode) allArguments[2];
            final boolean async = (boolean) allArguments[3];

            final SimplePropertyExpression<Object, Boolean> property = (SimplePropertyExpression<Object, Boolean>) expr;
            try {
                final @Nullable Boolean value = mode == Changer.ChangeMode.RESET
                        ? null : (boolean) delta[0];

                final List<T> entities = new ArrayList<>();
                for (final Object raw : property.getExpr().getArray(e))
                    entities.add((T) raw);

                for (final T entity : entities)
                    setter.set(entity, async, value);

            } catch (Exception ex) {
                if (ConfigManager.get("debug", false))
                    ex.printStackTrace();

                DiSkyRuntimeHandler.error(ex, ((INodeHolder) property).getNode());
            }
        }
    }


    //endregion

    public interface StateSetter<T> {

        /**
         * Set the state of the entity to the given value.
         * @param entity The entity to change the state of.
         * @param async Whether to set the state asynchronously. Blocking operations can be done in this case.
         * @param value The value to set the state to.
         */
        void set(T entity, boolean async, @Nullable Boolean value);
    }

}
