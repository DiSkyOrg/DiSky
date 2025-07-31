package net.itsthesky.disky.api.skript.reflects;

/*
 * DiSky
 * Copyright (C) 2025 ItsTheSky
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import lombok.Getter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.DiSkyRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.bytebuddy.matcher.ElementMatchers.named;

public final class ReflectChangeablePropertyFactory {

    private static final AtomicInteger COUNT = new AtomicInteger(0);

    public static <F, T> void registerChangeable(String fromTypeName,
                                                 String propertyName,
                                                 Class<F> fromType,
                                                 Class<T> toType,
                                                 String property,
                                                 Function<F, T> converter,
                                                 Function<Changer.ChangeMode, Class<?>[]> acceptedChanges,
                                                 BiFunction<F, ChangeData<T>, Void> changeApplier,
                                                 @Nullable ReflectClassFactory.Documentation documentation) {
        try {

            var builder = new ByteBuddy()
                    .redefine(ReflectChangeableProperty.class)
                    .name("net.itsthesky.diski.elements.reflects.ReflectChangeableProperty_" + COUNT.incrementAndGet());

            if (documentation != null) {
                builder = builder
                        .annotateType(AnnotationDescription.Builder.ofType(Name.class).define("value", documentation.getName()).build())
                        .annotateType(AnnotationDescription.Builder.ofType(Description.class).defineArray("value", documentation.getDescription()).build())
                        .annotateType(AnnotationDescription.Builder.ofType(Examples.class).defineArray("value", documentation.getExamples()).build())
                        .annotateType(AnnotationDescription.Builder.ofType(Since.class).defineArray("value", documentation.getSince()).build());
            }

            final Class<?> elementClass = builder
                    .method(named("convert")).intercept(MethodDelegation.to(new ReflectClassFactory.SingleConvertMethodInterceptor<>(converter)))
                    .method(named("getPropertyName")).intercept(MethodDelegation.to(new ReflectClassFactory.PropertyNameMethodInterceptor(propertyName)))
                    .method(named("acceptChange")).intercept(MethodDelegation.to(new AcceptChangeMethodInterceptor(acceptedChanges)))
                    .method(named("change")).intercept(MethodDelegation.to(new ChangeMethodInterceptor<>(changeApplier)))

                    .make()
                    .load(ReflectProperty.class.getClassLoader())
                    .getLoaded();

            DiSkyRegistry.registerProperty((Class<? extends Expression<T>>) elementClass,
                    toType, property, fromTypeName);
            DiSky.debug("Registered changeable property expression: " + elementClass.getName() + " (" + fromTypeName + " -> " + toType.getSimpleName() + " via \"" + property + "\")");

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Surcharge sans documentation
    public static <F, T> void registerChangeable(String fromTypeName,
                                                 String propertyName,
                                                 Class<F> fromType,
                                                 Class<T> toType,
                                                 String property,
                                                 Function<F, T> converter,
                                                 Function<Changer.ChangeMode, Class<?>[]> acceptedChanges,
                                                 BiFunction<F, ChangeData<T>, Void> changeApplier) {
        registerChangeable(fromTypeName, propertyName, fromType, toType, property, converter, acceptedChanges, changeApplier, null);
    }

    // Classe pour encapsuler les données de changement
    @Getter
    public static class ChangeData<T> {
        private final T[] delta;
        private final Changer.ChangeMode mode;
        private final Event event;

        public ChangeData(T[] delta, Changer.ChangeMode mode, Event event) {
            this.delta = delta;
            this.mode = mode;
            this.event = event;
        }

        public @Nullable T getFirstDelta() {
            return delta.length > 0 ? delta[0] : null;
        }
    }

    // Intercepteur pour acceptChange
    public static class AcceptChangeMethodInterceptor {
        private final Function<Changer.ChangeMode, Class<?>[]> acceptedChanges;

        public AcceptChangeMethodInterceptor(Function<Changer.ChangeMode, Class<?>[]> acceptedChanges) {
            this.acceptedChanges = acceptedChanges;
        }

        @RuntimeType
        public Object intercept(Changer.ChangeMode mode) {
            return acceptedChanges.apply(mode);
        }
    }

    // Intercepteur pour change
    public static class ChangeMethodInterceptor<F, T> {
        private final BiFunction<F, ChangeData<T>, Void> changeApplier;

        public ChangeMethodInterceptor(BiFunction<F, ChangeData<T>, Void> changeApplier) {
            this.changeApplier = changeApplier;
        }

        @RuntimeType
        public Object intercept(@This SimplePropertyExpression<F, T> expr,
                                @AllArguments Object[] args) {
            final Event event = (Event) args[0];
            final Object[] rawDelta = (Object[]) args[1];
            final Changer.ChangeMode mode = (Changer.ChangeMode) args[2];

            // Récupération de la cible via getExpr()
            Expression<F> sourceExpr = (Expression<F>) expr.getExpr();
            F[] targets = sourceExpr.getArray(event);

            // Cast et vérification du delta
            T[] delta = (T[]) rawDelta;

            // Application du changement sur chaque cible
            for (F target : targets) {
                ChangeData<T> changeData = new ChangeData<>(delta, mode, event);
                changeApplier.apply(target, changeData);
            }

            return null;
        }
    }

}
