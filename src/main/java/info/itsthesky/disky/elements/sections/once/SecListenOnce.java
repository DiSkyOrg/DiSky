package info.itsthesky.disky.elements.sections.once;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.api.events.DiSkyEvent;
import info.itsthesky.disky.api.events.SimpleDiSkyEvent;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.core.SkriptUtils;
import net.dv8tion.jda.api.events.GenericEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;

import static info.itsthesky.disky.core.SkriptUtils.async;

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

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult, @NotNull SectionNode sectionNode, @NotNull List<TriggerItem> list) {
        DiSky.debug("Starting to parse listen once section ...");
        if (!(expressions[0] instanceof VariableString)) {
            Skript.error("The event name in a listen once section must be a literal string!");
            return false;
        }

        final String rawEvent = ((VariableString) expressions[0]).toString().replace("\"", "");
        final Object result = SkriptParser.parseStatic(rawEvent, Skript.getEvents().iterator(), ParseContext.EVENT, "Cannot parse the given event: " + rawEvent);
        // DiSky.debug("debug 2: " + rawEvent + " / " + result + " / " + (result != null ? result.getClass().getSimpleName() : "null"));
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

            // now we parse
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

        final Bot bot = Bot.fromContext(exprBot, event);
        async(() -> {
            final var builder = bot.getInstance()
                    .listenOnce(eventClass);
            if (timeout != null && timeoutTrigger != null) {
                builder.timeout(Duration.ofMillis(timeout.getMilliSeconds()), () -> {
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

                    trigger.execute(simpleDiSkyEvent);
                } catch (Exception ex) {
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
}
