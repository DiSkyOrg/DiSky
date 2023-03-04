package info.itsthesky.disky.api.skript;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * This class is used to create a new class that extends {@link SimplePropertyExpression}
 * to "trick" Skript into thinking that the class is a property expression without manually creating thousands of classes.
 * @author ItsTheSky
 */
public final class ReflectClassFactory {

	public static class ConvertMethodInterceptor<F, T> {
		private final Function<F, T> function;
		public ConvertMethodInterceptor(Function<F, T> function) {
			this.function = function;
		}
		@RuntimeType
		public Object intercept(@AllArguments Object[] allArguments) {
			return function.apply((F) allArguments[0]);
		}
	}

	public static class PropertyNameMethodInterceptor {
		private final String propertyName;
		public PropertyNameMethodInterceptor(String propertyName) {
			this.propertyName = propertyName;
		}
		@RuntimeType
		public Object intercept(@AllArguments Object[] allArguments) {
			return propertyName;
		}
	}

	private static final AtomicInteger COUNT = new AtomicInteger();
	public static <F, T> void register(Class<F> fromType,
									   String fromTypeName,
									   String propertyName,
									   Class<T> toType,
									   String property,
									   Function<F, T> converter) {
		try {

			final Class<?> elementClass = new ByteBuddy()
					.redefine(ReflectProperty.class)
					.name("ReflectProperty_" + COUNT.incrementAndGet())

					.method(named("convert")).intercept(MethodDelegation.to(new ConvertMethodInterceptor<>(converter)))
					.method(named("getPropertyName")).intercept(MethodDelegation.to(new PropertyNameMethodInterceptor(propertyName)))

					.make()
					.load(ReflectProperty.class.getClassLoader())
					.getLoaded();

			SimplePropertyExpression.register((Class<? extends Expression<T>>) elementClass,
					toType, property, fromTypeName);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static class ReflectProperty extends SimplePropertyExpression<Object, Object> {

		@Override
		protected @NotNull String getPropertyName() {
			throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
		}

		@Override
		public @Nullable Object convert(Object entry) {
			throw new UnsupportedOperationException("This method should never be called! It is only here to make the compiler happy.");
		}

		@Override
		public @NotNull Class<?> getReturnType() {
			return Object.class;
		}
	}

}
