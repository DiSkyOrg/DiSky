package info.itsthesky.disky.api;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public abstract class PropertiesFactory {

    protected static  <F, T> void register(Class<F> from, Class<T> to, String property, Function<F, T> function) {
        new StupidSimplePropertyExpression<F, T>(property) {
            @Override
            public @Nullable
            T convert(F f) {
                return function.apply(f);
            }
        };
    }

}
