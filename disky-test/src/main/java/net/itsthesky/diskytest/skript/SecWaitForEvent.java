package net.itsthesky.diskytest.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.Section;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import net.dv8tion.jda.api.events.Event;
import net.itsthesky.disky.api.events.EventListener;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.api.events.rework.BuiltEvent;
import net.itsthesky.disky.api.events.rework.EventBuilder;
import net.itsthesky.disky.core.SkriptUtils;
import net.itsthesky.diskytest.framework.TestFixture;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Skript section:  {@code wait for [discord] event "<JDA-class-simple-name>":}
 *
 * <p>At parse time, resolves the given JDA event class simple name against
 * {@link EventBuilder#BUILT_EVENTS} and parses the section body in the matching
 * Bukkit event class context — so {@code event-message}, {@code event-channel},
 * {@code reply with}, etc. resolve just like in a production event handler.
 *
 * <p>At runtime (inside a {@code disky test}), registers a temporary DiSky
 * {@link EventListener} that constructs the expected {@link SimpleDiSkyEvent}
 * Bukkit wrapper and executes the parsed trigger. The listener is removed when
 * the enclosing test's fixture is cleaned up.
 */
public class SecWaitForEvent extends Section {

    static {
        Skript.registerSection(SecWaitForEvent.class,
                "wait for [discord] event %string%");
    }

    private String jdaClassSimpleName;
    private BuiltEvent<?> targetBuiltEvent;
    private Trigger bodyTrigger;

    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult, SectionNode sectionNode,
                        List<TriggerItem> triggerItems) {
        // Enforce a literal event name — we need to resolve it at parse time.
        if (!(exprs[0] instanceof Literal<?> literal)) {
            Skript.error("The event name in 'wait for event' must be a literal string, "
                    + "not a variable or computed expression.");
            return false;
        }
        Object raw = literal.getSingle();
        if (raw == null) {
            Skript.error("'wait for event' requires a non-null event name");
            return false;
        }
        jdaClassSimpleName = raw.toString().trim();

        targetBuiltEvent = findBuiltEvent(jdaClassSimpleName);
        if (targetBuiltEvent == null) {
            String available = EventBuilder.BUILT_EVENTS.stream()
                    .map(b -> b.getJdaEventClass().getSimpleName())
                    .sorted()
                    .distinct()
                    .collect(Collectors.joining(", "));
            Skript.error("Unknown JDA event class '" + jdaClassSimpleName
                    + "'. Must be the simple name of a JDA event class registered with DiSky. "
                    + "Available: " + available);
            return false;
        }

        Class<? extends org.bukkit.event.Event> bukkitEventClass = targetBuiltEvent.getBukkitEventClass();
        bodyTrigger = SkriptUtils.loadCode(
                sectionNode,
                this,
                "wait for " + targetBuiltEvent.getJdaEventClass().getSimpleName(),
                null,
                null,
                bukkitEventClass);

        return true;
    }

    private static @Nullable BuiltEvent<?> findBuiltEvent(String simpleName) {
        // Primary: case-insensitive simple-name match.
        List<BuiltEvent<?>> matches = EventBuilder.BUILT_EVENTS.stream()
                .filter(b -> b.getJdaEventClass().getSimpleName().equalsIgnoreCase(simpleName))
                .toList();
        if (matches.size() == 1) return matches.get(0);
        if (matches.size() > 1) {
            // Fallback: fully qualified name match to disambiguate.
            return EventBuilder.BUILT_EVENTS.stream()
                    .filter(b -> b.getJdaEventClass().getName().equalsIgnoreCase(simpleName))
                    .findFirst().orElse(matches.get(0));
        }
        // Fallback: try FQN match (user passed a full package-qualified name).
        return EventBuilder.BUILT_EVENTS.stream()
                .filter(b -> b.getJdaEventClass().getName().equalsIgnoreCase(simpleName))
                .findFirst().orElse(null);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected @Nullable TriggerItem walk(org.bukkit.event.Event event) {
        if (!(event instanceof EvtDiSkyTest.TestRunEvent testEvent)) {
            Skript.warning("'wait for event' executed outside of 'disky test' — ignored.");
            return getNext();
        }
        TestFixture fixture = testEvent.getFixture();
        BuiltEvent<?> built = targetBuiltEvent;
        Trigger body = bodyTrigger;

        Class<? extends Event> jdaClass = built.getJdaEventClass();
        String botName = fixture.getBot().getName();

        EventListener<? extends Event> listener = new EventListener(
                jdaClass,
                (java.util.function.BiConsumer<Event, net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent>)
                        (jdaEvent, logEvent) -> {
                            try {
                                org.bukkit.event.Event bukkitEvent = built.createBukkitInstance(jdaEvent);
                                body.execute(bukkitEvent);
                            } catch (Throwable t) {
                                fixture.recordError(
                                        "Failed to dispatch 'wait for event' section for "
                                                + jdaClass.getSimpleName() + ": " + t.getMessage());
                                t.printStackTrace();
                            }
                        },
                (java.util.function.Predicate<Event>) e -> true,
                (java.util.function.Predicate<net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent>) e -> true,
                null,
                botName,
                getParser().getNode());

        fixture.registerTemporaryListener(listener);
        return getNext();
    }

    @Override
    public String toString(@Nullable org.bukkit.event.Event e, boolean debug) {
        return "wait for event " + jdaClassSimpleName;
    }
}
