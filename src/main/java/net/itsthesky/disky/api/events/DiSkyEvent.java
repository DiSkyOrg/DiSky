package net.itsthesky.disky.api.events;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Config;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptEventInfo;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.log.SkriptLogger;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.disky.elements.sections.handler.DiSkyRuntimeHandler;
import net.itsthesky.disky.managers.CoreEventListener;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public abstract class DiSkyEvent<D extends net.dv8tion.jda.api.events.Event> extends SelfRegisteringSkriptEvent {

    private static final Map<Class<? extends DiSkyEvent<?>>, Class<? extends SimpleDiSkyEvent<?>>> externalEventMap = new HashMap<>();

    /**
     * The ending appended to patterns if no custom ending is specified
     */
    public static final String APPENDED_ENDING = "[seen by %-string%]";
    private final Map<Class<?>, Object> valueMap = new HashMap<>();
    private String stringRepresentation;
    private Trigger trigger;
    private EventListener<D> listener;
    private String bot;
    private Class<? extends Event> bukkitClass;
    private Class<D> jdaClass;
    private String originalName;
    private Class<? extends Event>[] originalEvents;
    private Constructor<?> constructor;

    /**
     * Enregistre un mapping externe entre une classe DiSkyEvent et sa classe Bukkit associée
     * Utilisé par le nouveau système de génération d'événements
     */
    public static <T extends net.dv8tion.jda.api.events.Event, D extends DiSkyEvent<T>, B extends SimpleDiSkyEvent<T>>
    void registerExternalEventClass(Class<D> diSkyEventClass, Class<B> bukkitEventClass) {
        externalEventMap.put(diSkyEventClass, bukkitEventClass);
    }

    /**
     * @param name     The name of the event used for ScriptLoader#setCurrentEvents
     * @param type     The class holding the event
     * @param clazz    The class holding the Bukkit event
     * @param patterns The patterns for the event
     */
    public static SkriptEventInfo<?> register(String name, Class type, Class clazz, String... patterns) {
        return register(name, APPENDED_ENDING, type, clazz, patterns);
    }

    /**
     * @param name     The name of the event used for ScriptLoader#setCurrentEvents
     * @param ending   The ending applied for checking the bot (which can be grabbed via BaseEvent.APPENDED_ENDING)
     * @param type     The class holding the event
     * @param clazz    The class holding the Bukkit event
     * @param patterns The patterns for the event
     */
    @SuppressWarnings("unchecked")
    public static <T extends SimpleDiSkyEvent<?>> SkriptEventInfo<?> register(String name, String ending, Class type, Class<T> clazz, String... patterns) {
        // Enregistrer le mapping externe pour les événements générés
        if (type.getName().contains("Generated")) {
            registerExternalEventClass(type, (Class) clazz);
        }

        for (int i = 0; i < patterns.length; i++) {
            patterns[i] += " " + ending;
        }
        return Skript.registerEvent(name, type, clazz, patterns);
    }

    /**
     * If you wanna check by yourself the event through a predicate.
     * <br> Return true by default and execute the event given either it complete specifics conditions or not.
     */
    protected Predicate<D> checker() {
        return e -> true;
    }

    protected Predicate<GuildAuditLogEntryCreateEvent> logChecker() {
        return e -> true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?> @NotNull [] exprs, int matchedPattern, @NotNull ParseResult parser) {
        bot = (String) (exprs[0] == null ? null : exprs[0].getSingle());

        // Check if we're dealing with a generated event class first
        if (externalEventMap.containsKey(this.getClass())) {
            this.bukkitClass = externalEventMap.get(this.getClass());

            // Get JDA event class from generic parameter
            try {
                jdaClass = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            } catch (ClassCastException e) {
                throw new RuntimeException(this.getClass().getCanonicalName() + " doesn't use a valid JDA event type. Report this at https://github.com/SkyCraft78/DiSky3/issues!");
            }

            // For external classes, we need to get a constructor that accepts the DiSkyEvent
            try {
                constructor = bukkitClass.getDeclaredConstructor(DiSkyEvent.class);
            } catch (NoSuchMethodException e) {
                DiSky.debug("No constructor found for " + bukkitClass.getName() + " with DiSkyEvent parameter. Looking for empty constructor...");
                try {
                    constructor = bukkitClass.getDeclaredConstructor();
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException("No suitable constructor found for " + bukkitClass.getName());
                }
            }
        } else {
            // Original approach - find inner class
            bukkitClass = (Class<? extends Event>) Arrays.stream(this.getClass().getDeclaredClasses())
                    .filter(innerClass -> innerClass.getSuperclass() == SimpleDiSkyEvent.class)
                    .findFirst()
                    .orElse(null);

            if (bukkitClass == null) {
                throw new RuntimeException(this.getClass().getCanonicalName() + " doesn't have an inner SimpleDiSkyEvent " +
                        "class to be instantiated. Report this at https://github.com/SkyCraft78/DiSky3/issues!");
            }

            try {
                jdaClass = (Class<D>) ((ParameterizedType) bukkitClass.getGenericSuperclass()).getActualTypeArguments()[0];
            } catch (ClassCastException e) {
                throw new RuntimeException(this.getClass().getCanonicalName() + "'s inner class doesn't use the same JDA" +
                        " event as it's parent class in it's SimpleDiSkyEvent. Report this at https://github.com/SkyCraft78/DiSky3/issues!");
            }

            try {
                constructor = bukkitClass.getDeclaredConstructor(this.getClass());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Constructor not found for " + bukkitClass.getName(), e);
            }
        }

        stringRepresentation = ScriptLoader.replaceOptions(SkriptLogger.getNode().getKey()) + ":";
        originalName = getParser().getCurrentEventName();
        originalEvents = getParser().getCurrentEvents();

        String name = null;
        for (SkriptEventInfo<?> event : Skript.getEvents()) {
            if (bukkitClass.equals(event.getElementClass())) {
                name = event.getName();
            }
        }

        getParser().setCurrentEvent(name == null ? "DiSky event" : name, bukkitClass);
        return true;
    }

    @Override
    public void afterParse(@NotNull Config config) {
        getParser().setCurrentEvent(originalName, originalEvents);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void register(@NotNull Trigger t) {
        trigger = t;
        listener = new EventListener<>(jdaClass, (JDAEvent, auditLogEntryCreateEvent) -> {
            if (check(JDAEvent)) {
                /* !? */
                SimpleDiSkyEvent<D> eventWorkaround = null;
                SimpleDiSkyEvent<D> event;
                try {
                    // Adapter pour les deux types de constructeurs
                    if (constructor.getParameterCount() == 1) {
                        eventWorkaround = (SimpleDiSkyEvent<D>) constructor.newInstance(DiSkyEvent.this);
                    } else {
                        eventWorkaround = (SimpleDiSkyEvent<D>) constructor.newInstance();
                    }
                } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                    DiSkyRuntimeHandler.error(new RuntimeException("Failed to instantiate event: " + e.getMessage(), e),
                            SkriptLogger.getNode());
                    return;
                }
                event = eventWorkaround;

                event.setJDAEvent(JDAEvent);
                event.setLogEvent(auditLogEntryCreateEvent);

                SkriptUtils.sync(() -> {
                    if (getTrigger() != null) {
                        getTrigger().execute(event);
                    }
                });
            }
        }, checker(), logChecker(), getLogType(), bot, getParser().getNode());
        CoreEventListener.addListener(listener);
    }

    @Override
    public void unregister(final @NotNull Trigger t) {
        if (listener != null) {
            listener.enabled = false;
            CoreEventListener.removeListener(listener);
        }

        listener = null;
        trigger = null;
    }

    @Override
    public void unregisterAll() {
        if (trigger != null)
            unregister(trigger);
    }

    @Override
    public @NotNull String toString(@NotNull Event e, boolean debug) {
        return stringRepresentation;
    }

    public String getBot() {
        return bot;
    }

    public Trigger getTrigger() {
        return trigger;
    }

    /**
     * Used to check whether the event is valid for the trigger to run.
     *
     * @param event The JDA event to be checked
     */
    public boolean check(D event) {
        return bot == null || bot.equalsIgnoreCase(DiSky.getManager().getJDAName(event.getJDA()));
    }

    public @Nullable ActionType getLogType() {
        return null;
    }

    public Class<? extends Event> getBukkitClass() {
        return bukkitClass;
    }

    public Class<D> getJDAClass() {
        return jdaClass;
    }

    public static Class<? extends net.dv8tion.jda.api.events.Event> getDiSkyEventType(Class<DiSkyEvent<?>> clazz) {
        try {
            return (Class<? extends net.dv8tion.jda.api.events.Event>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (ClassCastException e) {
            throw new RuntimeException(clazz.getCanonicalName() + " doesn't use the same JDA event as it's parent class.");
        }
    }
}