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

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.itsthesky.disky.api.events.specific.ComponentInteractionEvent;
import net.itsthesky.disky.api.events.specific.LogEvent;
import net.itsthesky.disky.api.events.specific.MessageEvent;
import net.itsthesky.disky.api.events.specific.ModalEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A fluent builder for configuring and registering DiSky events.
 * This allows for more concise and maintainable event registration.
 *
 * @param <T> The JDA event type
 * @author ItsTheSky
 */
public class EventBuilder<T extends Event> {
    private final Class<T> jdaEventClass;
    private String name;
    private String[] patterns;
    private final List<String> descriptionLines = new ArrayList<>();
    private final List<String> exampleLines = new ArrayList<>();
    private final List<EventValueRegistration<T, ?>> valueRegistrations = new ArrayList<>();
    private final List<RestValueRegistration<T, ?, ?>> restValueRegistrations = new ArrayList<>();
    private final List<InterfaceRegistration<T, ?, ?, ?>> interfaces = new ArrayList<>();
    private Function<T, Guild> authorMapper;
    private Predicate<T> checker;
    private Predicate<GuildAuditLogEntryCreateEvent> logChecker;

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
        Collections.addAll(this.descriptionLines, lines);
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
        this.exampleLines.addAll(Arrays.asList(examples));
        return this;
    }

    /**
     * Implements an interface for this event.
     * This allows providing specific functionalities to the event through a given interface.
     *
     * @param <I> The type of the interface to implement
     * @param <R> The return type of the interface method</r>
     * @param <P> The parameter type of the interface method (can be null)
     * @param interfaceClass The class of the interface to implement
     * @param returnTypeClass The class of the return type
     * @param parameterTypeClass The class of the parameter type (null if no parameter)
     * @param methodName The name of the method to implement
     * @param function The function that implements the interface method
     * @return This builder instance
     */
    public <I, R, P> EventBuilder<T> implement(Class<I> interfaceClass, Class<R> returnTypeClass, @Nullable Class<P> parameterTypeClass,
                                               String methodName, BiFunction<P, T, R> function) {
        interfaces.add(new InterfaceRegistration<>(interfaceClass, returnTypeClass, parameterTypeClass, methodName, function));
        return this;
    }

    public EventBuilder<T> implementModal(BiFunction<T, Modal, ModalCallbackAction> modalMapper) {
        return implement(ModalEvent.class, ModalCallbackAction.class, Modal.class, "replyModal",
                (modal, event) -> modalMapper.apply(event, modal));
    }

    public EventBuilder<T> implementInteraction(Function<T, GenericInteractionCreateEvent> interactionMapper) {
        return implement(ModalEvent.class, GenericInteractionCreateEvent.class, null, "getInteractionEvent",
                (none, event) -> interactionMapper.apply(event));
    }

    public EventBuilder<T> implementMessage(Function<T, MessageChannel> messageMapper) {
        return implement(MessageEvent.class, MessageChannel.class, null, "getMessageChannel",
                (none, event) -> messageMapper.apply(event));
    }

    public EventBuilder<T> implementLog(Function<T, User> authorMapper) {
        return implement(LogEvent.class, User.class, null, "getActionAuthor",
                (none, event) -> authorMapper.apply(event));
    }

    public EventBuilder<T> implementComponentInteraction(Function<T, ComponentInteraction> interactionMapper) {
        return implement(ComponentInteractionEvent.class, ComponentInteraction.class, null, "getInteractionEvent",
                (none, event) -> interactionMapper.apply(event));
    }

    /**
     * Sets a checker for the event. It will be called before the event is executed
     * to determine if the event should be executed or not.
     * @param checker The checker function
     * @return This builder
     */
    public EventBuilder<T> checker(Predicate<T> checker) {
        this.checker = checker;
        return this;
    }

    /**
     * Sets a checker for the log event. It will be called before the event is executed
     * to determine if the event should be executed or not.
     * @param checker The checker function
     * @return This builder
     */
    public EventBuilder<T> logChecker(Predicate<GuildAuditLogEntryCreateEvent> checker) {
        this.logChecker = checker;
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
     * Registers all channel-related values to be accessed in scripts.
     * For the given channel mapper, the following registrations will be made:
     * <ul>
     *   <li>Channel</li>
     *   <li>MessageChannel / AudioChannel</li>
     *   <li>VoiceChannel / StageChannel</li>
     *   <li>PrivateChannel / GuildChannel</li>
     *   <li>TextChannel / NewsChannel / ThreadChannel / ForumChannel</li>
     * </ul>
     *
     * @param channelMapper a function to extract the channel from the JDA event
     * @return this builder
     */
    public EventBuilder<T> channelValues(Function<T, Channel> channelMapper) {
        value(Channel.class, channelMapper);

        value(MessageChannel.class, channelMapper.andThen(channel -> channel.getType().isMessage() ? (MessageChannel) channel : null));
        value(AudioChannel.class, channelMapper.andThen(channel -> channel.getType().isAudio() ? (AudioChannel) channel : null));

        value(VoiceChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().equals(ChannelType.VOICE) ? (VoiceChannel) channel : null));
        value(StageChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().equals(ChannelType.STAGE) ? (StageChannel) channel : null));

        value(PrivateChannel.class, channelMapper.andThen(channel -> !channel.getType().isGuild() ? (PrivateChannel) channel : null));
        value(GuildChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() ? (GuildChannel) channel : null));

        value(TextChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().equals(ChannelType.TEXT) ? (TextChannel) channel : null));
        value(NewsChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().equals(ChannelType.NEWS) ? (NewsChannel) channel : null));
        value(ThreadChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().isThread() ? (ThreadChannel) channel : null));
        value(ForumChannel.class, channelMapper.andThen(channel -> channel.getType().isGuild() && channel.getType().equals(ChannelType.FORUM) ? (ForumChannel) channel : null));

        return this;
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

    List<InterfaceRegistration<T, ?, ?, ?>> getInterfaces() {
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

    Predicate<T> getChecker() {
        return checker;
    }

    Predicate<GuildAuditLogEntryCreateEvent> getLogChecker() {
        return logChecker;
    }
}