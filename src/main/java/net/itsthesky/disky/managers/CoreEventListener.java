package net.itsthesky.disky.managers;

import net.itsthesky.disky.core.Bot;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.hooks.EventListener;

import java.util.ArrayList;

public class CoreEventListener implements EventListener {

    public final static ArrayList<net.itsthesky.disky.api.events.EventListener<?>> AllRegisteredListeners = new ArrayList<>();

    private final Bot bot;
    public CoreEventListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof final GuildAuditLogEntryCreateEvent e)
            AllRegisteredListeners.forEach(listener -> listener.onGuildAuditLogEntryCreate(e));

        for (final net.itsthesky.disky.api.events.EventListener<?> listener : AllRegisteredListeners)
            listener.onGenericEvent(event);
    }

    public static <D extends Event> void addListener(net.itsthesky.disky.api.events.EventListener<D> listener) {
        AllRegisteredListeners.add(listener);
    }

    public static <D extends Event> void removeListener(net.itsthesky.disky.api.events.EventListener<D> listener) {
        AllRegisteredListeners.remove(listener);
    }

}
