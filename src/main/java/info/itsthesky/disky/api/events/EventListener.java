package info.itsthesky.disky.api.events;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.core.Bot;
import info.itsthesky.disky.managers.BotManager;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Made by Blitz, minor edit by Sky for DiSky
 */
public class EventListener<T> extends ListenerAdapter {

    public final static ArrayList<EventListener<?>> listeners = new ArrayList<>();
    public boolean enabled = true;
    private final Class<T> clazz;
    private final BiConsumer<T, GuildAuditLogEntryCreateEvent> consumer;
    private final Predicate<T> checker;

    private final @Nullable String specificBotName;
    private final boolean isWaitingLogEvent;
    private final @Nullable ActionType logType;
    private final Predicate<GuildAuditLogEntryCreateEvent> logChecker;

    private @Nullable T lastEvent;

    public EventListener(Class<T> paramClass,
                         BiConsumer<T, GuildAuditLogEntryCreateEvent> consumer,
                         Predicate<T> checker, Predicate<GuildAuditLogEntryCreateEvent> logChecker,
                         @Nullable ActionType actionType, @Nullable String specificBotName) {
        this.clazz = paramClass;
        this.consumer = consumer;
        this.checker = checker;
        this.logChecker = logChecker;
        this.specificBotName = specificBotName;

        this.isWaitingLogEvent = actionType != null;
        this.logType = actionType;
    }

    public static void addListener(EventListener<?> listener) {
        removeListener(listener);
        listeners.add(listener);
    }

    public static void removeListener(EventListener<?> listener) {
        listeners.remove(listener);
        DiSky.getManager().execute(bot -> bot.getInstance().removeEventListener(listener));
    }

    public static void registerAll(Bot bot) {
        listeners.forEach(listener -> {
            if (listener.specificBotName != null && !listener.specificBotName.equalsIgnoreCase(bot.getName()))
                return;

            DiSky.debug("Registering event listener " + listener.clazz.getSimpleName() + " for bot " + bot.getName() + listener.hash());
            bot.getInstance().addEventListener(listener);
        });
    }

    @Override
    public void onGuildAuditLogEntryCreate(GuildAuditLogEntryCreateEvent event) {
        DiSky.debug("received log event " + event.getEntry().getType() + " by DiSky.");
        if (isWaitingLogEvent && event.getEntry().getType() == logType) {
            DiSky.debug("Log event " + event.getEntry().getType() + " received by DiSky. Is there last event? " + (lastEvent != null) + ".");
            if (lastEvent != null) {
                if (logChecker.test(event)) {
                    consumer.accept(lastEvent, event);
                    lastEvent = null;
                }
            } else {
                DiSky.getInstance().getLogger().severe("The last event is null, but the log event is waiting for it! " +
                        "(ActionType: " + event.getEntry().getType() + ", Event: " + event + ")");
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (enabled && clazz.isInstance(event)) {
            DiSky.debug("Event " + event.getClass().getSimpleName() + " received by DiSky. Is it valid? " + checker.test((T) event) + "." + hash());
            if (!checker.test((T) event))
                return;
            DiSky.debug("- Event is valid, executing consumer (is waiting for log event: " + isWaitingLogEvent + ")");
            if (isWaitingLogEvent)  {
                lastEvent = (T) event;
            } else {
                consumer.accept((T) event, null);
            }
        }
    }

    public String hash() {
        return " [class hash: " + this.hashCode() + "]";
    }

    public Class<T> getClazz() {
        return clazz;
    }
}