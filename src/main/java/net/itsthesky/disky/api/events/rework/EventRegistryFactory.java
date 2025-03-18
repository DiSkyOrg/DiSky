package net.itsthesky.disky.api.events.rework;

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

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.dv8tion.jda.api.events.Event;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.SkriptUtils;

import java.nio.channels.Channel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Factory class that generates DiSky event classes dynamically.
 * This reduces boilerplate code and makes event registration more centralized.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EventRegistryFactory {

    private static final AtomicInteger EVENT_COUNT = new AtomicInteger(0);

    /**
     * Creates a new event builder for the specified JDA event class.
     *
     * @param eventClass The JDA event class
     * @return An event builder
     */
    public static <T extends Event> EventBuilder<T> builder(Class<T> eventClass) {
        return new EventBuilder<>(eventClass);
    }

    /**
     * Registers an event based on the configuration in the EventBuilder.
     *
     * @param builder The event builder with configuration
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T extends Event> void registerEvent(EventBuilder<T> builder) {
        try {
            // Generate unique class names
            String baseClassName = "net.itsthesky.disky.elements.events.generated.Generated" +
                    builder.getJdaEventClass().getSimpleName();
            String diSkyEventClassName = baseClassName + "_" + EVENT_COUNT.incrementAndGet();
            String bukkitEventClassName = diSkyEventClassName + "$BukkitEvent";

            // Create DiSkyEvent subclass
            Class<?> diSkyEventClass = new ByteBuddy()
                    .subclass(TypeDescription.Generic.Builder.parameterizedType(
                                    DiSkyEvent.class,
                                    builder.getJdaEventClass())
                            .build())
                    .name(diSkyEventClassName)
                    .annotateType(AnnotationDescription.Builder.ofType(Name.class)
                            .define("value", builder.getName()).build())
                    .annotateType(AnnotationDescription.Builder.ofType(Description.class)
                            .defineArray("value", builder.getDescriptionLines()).build())
                    .annotateType(AnnotationDescription.Builder.ofType(Examples.class)
                            .defineArray("value", builder.getExampleLines()).build())
                    .make()
                    .load(DiSkyEvent.class.getClassLoader())
                    .getLoaded();

            final var interfaces = builder.getInterfaces();
            var simpleEventClassBuilder = new ByteBuddy()
                    .subclass(SimpleDiSkyEvent.class)
                    .name(bukkitEventClassName);

            // Now add all the interfaces
            for (final var inter : interfaces) {
                simpleEventClassBuilder = simpleEventClassBuilder.implement(inter.getInterfaceClass())
                        .defineMethod(inter.getMethodName(), inter.getReturnTypeClass(), 0)
                        .intercept(MethodDelegation.to(new BasicFunctionInterceptor<>(
                                inter.getFunction()
                        )));
            }

            Class<?> bukkitEventClass = simpleEventClassBuilder.make()
                    .load(diSkyEventClass.getClassLoader())
                    .getLoaded();

            DiSkyEvent.registerExternalEventClass((Class) diSkyEventClass, (Class) bukkitEventClass);

            // Register the event with Skript
            DiSkyEvent.register(
                    builder.getName(),
                    diSkyEventClass,
                    bukkitEventClass,
                    builder.getPatterns()
            ).description(builder.getDescriptionLines());

            // Register bot value
            SkriptUtils.registerBotValue((Class<? extends SimpleDiSkyEvent>) bukkitEventClass);

            // Register event values
            var hasChannelClass = builder.getValueRegistrations().stream()
                    .anyMatch(registration -> registration.getValueClass().equals(Channel.class));
            for (EventValueRegistration<T, ?> registration : builder.getValueRegistrations()) {
                final Class<?> valueClass = registration.getValueClass();
                final Function<T, ?> mapper = registration.getMapper();
                final int time = registration.getTime();

                SkriptUtils.registerValue(
                        (Class<? extends org.bukkit.event.Event>) bukkitEventClass,
                        (Class) valueClass,
                        event -> {
                            final var rawEvent = (SimpleDiSkyEvent) event;
                            return mapper.apply((T) rawEvent.getJDAEvent());
                        },
                        time
                );

                if (!hasChannelClass && Channel.class.isAssignableFrom(valueClass)) { // We also register the Channel class as it's a common type
                    SkriptUtils.registerValue(
                            (Class<? extends org.bukkit.event.Event>) bukkitEventClass,
                            Channel.class,
                            event -> {
                                final var rawEvent = (SimpleDiSkyEvent) event;
                                return (Channel) mapper.apply((T) rawEvent.getJDAEvent());
                            },
                            time
                    );

                    hasChannelClass = true;
                }
            }

            // Register rest values
            for (RestValueRegistration<T, ?, ?> registration : builder.getRestValueRegistrations()) {
                final String codeName = registration.getCodeName();
                final Function actionMapper =
                        registration.getActionMapper();
                final Function resultMapper = registration.getResultMapper();

                SkriptUtils.registerRawRestValue(
                        codeName,
                        (Class) bukkitEventClass,
                        event -> actionMapper.apply(((SimpleDiSkyEvent) event).getJDAEvent()),
                        resultMapper::apply
                );
            }

            // Register author value if specified
            if (builder.getAuthorMapper() != null) {
                SkriptUtils.registerAuthorValue(
                        (Class<? extends SimpleDiSkyEvent>) bukkitEventClass,
                        event -> builder.getAuthorMapper().apply(((SimpleDiSkyEvent<T>) event).getJDAEvent())
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to register event: " + builder.getName(), e);
        }
    }

    protected static class BasicFunctionInterceptor<I, S extends Event, R> {

        private final BiFunction<I, S, R> function;
        public BasicFunctionInterceptor(BiFunction<I, S, R> function) {
            this.function = function;
        }

        @RuntimeType
        public Object intercept(@AllArguments Object[] allArguments,
                                @This Object instance) {
            final var arg = allArguments.length > 0 ? allArguments[0] : null;
            final var event = (SimpleDiSkyEvent<S>) instance;
            if (arg == null)
                return function.apply(null, event.getJDAEvent());

            return function.apply((I) arg, event.getJDAEvent());
        }
    }

}