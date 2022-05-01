package info.itsthesky.disky.api.events;

import info.itsthesky.disky.DiSky;
import info.itsthesky.disky.managers.BotManager;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Made by Blitz, minor edit by Sky for DiSky
 */
public class EventListener<T> extends ListenerAdapter {

    public final static ArrayList<EventListener<?>> listeners = new ArrayList<>();
    public boolean enabled = true;
    private final Class<T> clazz;
    private final Consumer<T> consumer;
    private final Predicate<T> checker;

    public EventListener(Class<T> paramClass, Consumer<T> consumer, Predicate<T> checker) {
        this.clazz = paramClass;
        this.consumer = consumer;
        this.checker = checker;
    }

    public static void addListener(EventListener<?> listener) {
        removeListener(listener);
        listeners.add(listener);
        DiSky.getManager().registerGlobalListener(listener);
    }

    public static void removeListener(EventListener<?> listener) {
        listeners.remove(listener);
        DiSky.getManager().execute(bot -> bot.getInstance().removeEventListener(listener));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (enabled && clazz.isInstance(event)) {
            if (!checker.test((T) event))
                return;
            consumer.accept((T) event);
        }
    }

}