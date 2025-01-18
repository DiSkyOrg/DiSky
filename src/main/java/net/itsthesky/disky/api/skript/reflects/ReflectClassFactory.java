package net.itsthesky.disky.api.skript.reflects;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyRegistry;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

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

	public static class ClassResultMethodInterceptor {
		private final Class<?> eventClass;
		public ClassResultMethodInterceptor(Class<?> eventClass) {
			this.eventClass = eventClass;
		}
		@RuntimeType
		public Object intercept(@AllArguments Object[] allArguments) {
			return eventClass;
		}
	}

	public static class Documentation {

		private final String name;
		private final String[] description;
		private final String[] examples;
		private final String[] since;

		public Documentation(String name, String description, String examples, String... since) {
			this.name = name;
			this.description = description.split("\n");
			this.examples = examples.split("\n");
			this.since = since;
		}

		public String getName() {
			return name;
		}

		public String[] getDescription() {
			return description;
		}

		public String[] getExamples() {
			return examples;
		}

		public String[] getSince() {
			return since;
		}
	}

	private static final AtomicInteger COUNT = new AtomicInteger();
	public static <F, T> void register(String fromTypeName,
									   String propertyName,
									   Class<T> toType,
									   String property,
									   Function<F, T> converter,
									   Documentation documentation) {
		try {

			final Class<?> elementClass = new ByteBuddy()
					.redefine(ReflectProperty.class)
					.name("net.itsthesky.disky.elements.reflects.ReflectProperty_" + COUNT.incrementAndGet())

					.annotateType(AnnotationDescription.Builder.ofType(Name.class).define("value", documentation.getName()).build())
					.annotateType(AnnotationDescription.Builder.ofType(Description.class).defineArray("value", documentation.getDescription()).build())
					.annotateType(AnnotationDescription.Builder.ofType(Examples.class).defineArray("value", documentation.getExamples()).build())
					.annotateType(AnnotationDescription.Builder.ofType(Since.class).defineArray("value", documentation.getSince()).build())

					.method(named("convert")).intercept(MethodDelegation.to(new ConvertMethodInterceptor<>(converter)))
					.method(named("getPropertyName")).intercept(MethodDelegation.to(new PropertyNameMethodInterceptor(propertyName)))

					.make()
					.load(ReflectProperty.class.getClassLoader())
					.getLoaded();

			DiSkyRegistry.registerProperty((Class<? extends Expression<T>>) elementClass,
					toType, property, fromTypeName);
			DiSky.debug("Registered property expression: " + elementClass.getName() + " (" + fromTypeName + " -> " + toType.getSimpleName() + ")");

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
