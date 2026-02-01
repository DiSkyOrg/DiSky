package net.itsthesky.disky.elements.sections.once;

import ch.njol.skript.Skript;
import net.itsthesky.disky.api.DiSkyRegistry;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.iterator.ConsumingIterator;
import net.itsthesky.disky.DiSky;
import net.itsthesky.disky.api.events.DiSkyEvent;
import net.itsthesky.disky.api.events.SimpleDiSkyEvent;
import net.itsthesky.disky.core.Bot;
import net.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.events.GenericEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.structure.Structure;
import org.skriptlang.skript.lang.structure.StructureInfo;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static net.itsthesky.disky.core.SkriptUtils.async;

public class SecListenOnce extends Section {

    static {
        Skript.registerSection(
                SecListenOnce.class,
                "listen once to [the] [event] %string% [with [the] timeout %-timespan%] [(using|with) [the] %-bot%]"
        );
    }

    private Expression<Timespan> exprTimeout;
    private Expression<Bot> exprBot;

    private Class<? extends GenericEvent> eventClass;
    private Class<? extends DiSkyEvent> diSkyEventClass;
    private Class<? extends SimpleDiSkyEvent<?>> simpleEventClass;

    private Trigger trigger;
    private @Nullable Trigger timeoutTrigger;
    private @Nullable Event originalEvent;
    private @Nullable Class<? extends Event>[] currentEvents;

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> list) {
        if (true) {
            Skript.warning("The 'listen once' section is currently unavailable in Skript 2.9.1 and +. It'll come back once a fix from SkriptLang has been made about parsing an event.");
            return false;
        }

        //DiSky.debug("Starting to parse listen once section ...");
        if (!(expressions[0] instanceof VariableString)) {
            Skript.error("The event name in a listen once section must be a literal string!");
            return false;
        }
        currentEvents = getParser().getCurrentEvents();

        final String rawEvent = ((VariableString) expressions[0]).toString().replace("\"", "");
        final var events = new ConsumingIterator<>(Skript.getEvents().stream()
                .filter(e -> DiSkyEvent.class.isAssignableFrom(e.elementClass))
                .iterator(), event -> {
            final Structure.StructureData structureData = getParser().getData(Structure.StructureData.class);

            try {
                final var field = structureData.getClass().getDeclaredField("structureInfo");
                final var structInfo = new StructureInfo(event.patterns, event.elementClass, event.originClassPath, event.entryValidator);
                field.setAccessible(true);
                field.set(structureData, structInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        final Object result = SkriptParser.parseStatic(rawEvent, events,
                ParseContext.EVENT, "Cannot parse the given event: " + rawEvent);
        if (!(result instanceof SkriptEvent))
            return false;
        final SkriptEvent skriptEvent = (SkriptEvent) result;
        if(!(skriptEvent instanceof DiSkyEvent)) {
            Skript.error("The event name in a listen once section must be a DiSky event!");
            return false;
        }

        eventClass = ((DiSkyEvent) result).getJDAClass();
        diSkyEventClass = ((DiSkyEvent) result).getClass();
        simpleEventClass = ((DiSkyEvent) result).getBukkitClass();

        exprTimeout = (Expression<Timespan>) expressions[1];
        exprBot = (Expression<Bot>) expressions[2];

        boolean deep;
        if (exprTimeout == null) {
            deep = false;
            getParser().getCurrentSections().add(this);
            final List<TriggerItem> items = SkriptUtils.loadCode(sectionNode);
            trigger =  new Trigger(getParser().getCurrentScript(), eventClass.getSimpleName(),
                    skriptEvent, items);
        } else {
            sectionNode.convertToEntries(0);
            final Node subNode = sectionNode.get("on subscribe");
            if (!(subNode instanceof SectionNode)) {
                Skript.error("Cannot find the 'on subscribe' section in the listen once section!");
                return false;
            }

            final Node subNode2 = sectionNode.get("on timeout");
            if (!(subNode2 instanceof SectionNode)) {
                Skript.error("Cannot find the 'on timeout' section in the listen once section!");
                return false;
            }

            // prepare stuff
            getParser().getCurrentSections().add(this);

            // We can't allow event-related expressions or effects inside
            // the section. Although the given element will parse correctly,
            // the given event upon execution will be the subscribe event, and
            // not the 'outer' event.
            /*var events = new ArrayList<>(Arrays.asList(currentEvents));
            events.add((Class<? extends Event>) eventClass);
            getParser().setCurrentEvents(events.toArray(new Class[0]));*/
            getParser().setCurrentEvent("subscribe event", simpleEventClass);

            // parse the code
            final List<TriggerItem> items = SkriptUtils.loadCode((SectionNode) subNode);
            trigger = new Trigger(getParser().getCurrentScript(), eventClass.getSimpleName(),
                    skriptEvent, items);

            final List<TriggerItem> items2 = SkriptUtils.loadCode((SectionNode) subNode2);
            timeoutTrigger = new Trigger(getParser().getCurrentScript(), eventClass.getSimpleName() + " timeout",
                    skriptEvent, items2);

            deep = true;
        }
        DiSky.debug("Parsed listen once section with event " + eventClass.getSimpleName() + " (Has timeout section? " + (timeoutTrigger != null) + ", is deep? " + deep + ")");

        return true;
    }

    @Override
    @Nullable
    protected TriggerItem walk(@NotNull Event event) {
        Timespan timeout = exprTimeout == null ? null : exprTimeout.getSingle(event);
        originalEvent = event;

        final Bot bot = Bot.fromContext(exprBot, event);
        async(() -> {
            final var builder = bot.getInstance()
                    .listenOnce(eventClass);
            if (timeout != null && timeoutTrigger != null) {
                builder.timeout(Duration.ofMillis(timeout.getAs(Timespan.TimePeriod.MILLISECOND)), () -> {
                    try {
                        timeoutTrigger.execute(event);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }

            final var task = builder.subscribe(e -> {

                try {
                    final DiSkyEvent<?> newDiSkyEvent = (DiSkyEvent<?>) diSkyEventClass.getConstructor().newInstance();
                    final SimpleDiSkyEvent simpleDiSkyEvent = simpleEventClass.getConstructor(newDiSkyEvent.getClass()).newInstance(newDiSkyEvent);
                    simpleDiSkyEvent.setJDAEvent((net.dv8tion.jda.api.events.Event) e);

                    Variables.setLocalVariables(simpleDiSkyEvent, Variables.copyLocalVariables(event));
                    trigger.execute(simpleDiSkyEvent);
                } catch (Exception ex) {
                    System.out.println("Error while executing the event: ");
                    throw new RuntimeException(ex);
                }

            });

            try {
                task.get();
            } catch (CompletionException ex) {
                if (!(ex.getCause() instanceof TimeoutException)) {
                    ex.printStackTrace();
                }
            }
        });
        return getNext();
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "listen once to " + eventClass.getSimpleName() + " with timeout " + exprTimeout.toString(event, b);
    }

    public Event getOuterEvent() {
        return originalEvent;
    }

    public @Nullable Class<? extends Event>[] getCurrentEvents() {
        return currentEvents;
    }

    public <T> T executeInOuter(@NotNull Supplier<T> supplier) {
        var oldEvents = getParser().getCurrentEvents();
        getParser().setCurrentEvents(currentEvents);

        final T result = supplier.get();

        getParser().setCurrentEvents(oldEvents);
        return result;
    }
}
