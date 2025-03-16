package net.itsthesky.disky.api.events.rework;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A fluent builder for configuring and registering DiSky events.
 * This allows for more concise and maintainable event registration.
 *
 * @param <T> The JDA event type
 */
public class EventBuilder<T extends Event> {
    private final Class<T> jdaEventClass;
    private String name;
    private String[] patterns;
    private final List<String> descriptionLines = new ArrayList<>();
    private final List<String> exampleLines = new ArrayList<>();
    private final List<Class<?>> interfaces = new ArrayList<>();
    private final List<EventValueRegistration<T, ?>> valueRegistrations = new ArrayList<>();
    private final List<RestValueRegistration<T, ?, ?>> restValueRegistrations = new ArrayList<>();
    private Function<T, Guild> authorMapper;

    EventBuilder(Class<T> jdaEventClass) {
        this.jdaEventClass = jdaEventClass;
    }

    /**
     * Sets the name of the event.
     *
     * @param name The event name
     * @return This builder
     */
    public EventBuilder<T> name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the syntax patterns for the event.
     *
     * @param patterns The syntax patterns
     * @return This builder
     */
    public EventBuilder<T> patterns(String... patterns) {
        this.patterns = patterns;
        return this;
    }

    /**
     * Adds a description line for the event.
     *
     * @param line The description line
     * @return This builder
     */
    public EventBuilder<T> description(String line) {
        this.descriptionLines.add(line);
        return this;
    }

    /**
     * Adds multiple description lines for the event.
     *
     * @param lines The description lines
     * @return This builder
     */
    public EventBuilder<T> description(String... lines) {
        for (String line : lines) {
            this.descriptionLines.add(line);
        }
        return this;
    }

    /**
     * Adds an example for the event.
     *
     * @param example The example
     * @return This builder
     */
    public EventBuilder<T> example(String example) {
        this.exampleLines.add(example);
        return this;
    }

    /**
     * Adds multiple examples for the event.
     *
     * @param examples The examples
     * @return This builder
     */
    public EventBuilder<T> examples(String... examples) {
        for (String example : examples) {
            this.exampleLines.add(example);
        }
        return this;
    }

    /**
     * Specifies that the event should implement the given interface.
     *
     * @param interfaceClass The interface to implement
     * @return This builder
     */
    public EventBuilder<T> implement(Class<?> interfaceClass) {
        this.interfaces.add(interfaceClass);
        return this;
    }

    /**
     * Registers a value that can be accessed in scripts.
     *
     * @param valueClass The class of the value
     * @param mapper A function to extract the value from the JDA event
     * @return This builder
     */
    public <V> EventBuilder<T> value(Class<V> valueClass, Function<T, V> mapper) {
        return value(valueClass, mapper, 0);
    }

    /**
     * Registers a value that can be accessed in scripts, with a specific time.
     *
     * @param valueClass The class of the value
     * @param mapper A function to extract the value from the JDA event
     * @param time The time (-1 for past, 0 for present, 1 for future)
     * @return This builder
     */
    public <V> EventBuilder<T> value(Class<V> valueClass, Function<T, V> mapper, int time) {
        valueRegistrations.add(new EventValueRegistration<>(valueClass, mapper, time));
        return this;
    }

    /**
     * Registers past value (time = -1) that can be accessed in scripts.
     *
     * @param valueClass The class of the value
     * @param mapper A function to extract the value from the JDA event
     * @return This builder
     */
    public <V> EventBuilder<T> pastValue(Class<V> valueClass, Function<T, V> mapper) {
        return value(valueClass, mapper, -1);
    }

    /**
     * Registers future value (time = 1) that can be accessed in scripts.
     *
     * @param valueClass The class of the value
     * @param mapper A function to extract the value from the JDA event
     * @return This builder
     */
    public <V> EventBuilder<T> futureValue(Class<V> valueClass, Function<T, V> mapper) {
        return value(valueClass, mapper, 1);
    }

    /**
     * Registers the author value for this event.
     *
     * @param mapper A function to extract the guild from the JDA event
     * @return This builder
     */
    public EventBuilder<T> author(Function<T, Guild> mapper) {
        this.authorMapper = mapper;
        return this;
    }

    /**
     * Registers a REST value that can be accessed in scripts.
     * This is primarily used for values that need to be retrieved asynchronously
     * via JDA's RestAction API.
     *
     * @param codeName The code name used in 'event-codename' expressions
     * @param actionMapper A function to extract the RestAction from the JDA event
     * @return This builder
     */
    public <A> EventBuilder<T> restValue(String codeName, Function<T, RestAction<A>> actionMapper) {
        restValueRegistrations.add(new RestValueRegistration<>(codeName, actionMapper));
        return this;
    }

    /**
     * Registers a REST value that can be accessed in scripts, with a custom result mapper.
     * This is primarily used for values that need to be retrieved asynchronously
     * via JDA's RestAction API and then transformed to a different type.
     *
     * @param codeName The code name used in 'event-codename' expressions
     * @param actionMapper A function to extract the RestAction from the JDA event
     * @param resultMapper A function to map the result of the RestAction
     * @return This builder
     */
    public <A, R> EventBuilder<T> restValue(String codeName, Function<T, RestAction<A>> actionMapper, Function<A, R> resultMapper) {
        restValueRegistrations.add(new RestValueRegistration<>(codeName, actionMapper, resultMapper));
        return this;
    }

    /**
     * Registers the event with DiSky.
     */
    public void register() {
        EventRegistryFactory.registerEvent(this);
    }

    // Getters for internal use

    String getName() {
        return name;
    }

    String[] getPatterns() {
        return patterns;
    }

    String[] getDescriptionLines() {
        return descriptionLines.toArray(new String[0]);
    }

    String[] getExampleLines() {
        return exampleLines.toArray(new String[0]);
    }

    List<Class<?>> getInterfaces() {
        return interfaces;
    }

    List<EventValueRegistration<T, ?>> getValueRegistrations() {
        return valueRegistrations;
    }

    List<RestValueRegistration<T, ?, ?>> getRestValueRegistrations() {
        return restValueRegistrations;
    }

    Function<T, Guild> getAuthorMapper() {
        return authorMapper;
    }

    Class<T> getJdaEventClass() {
        return jdaEventClass;
    }
}